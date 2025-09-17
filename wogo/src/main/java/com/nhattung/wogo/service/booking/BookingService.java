package com.nhattung.wogo.service.booking;

import com.nhattung.wogo.constants.WogoConstants;
import com.nhattung.wogo.dto.request.*;
import com.nhattung.wogo.dto.response.*;
import com.nhattung.wogo.entity.*;
import com.nhattung.wogo.enums.BookingStatus;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.enums.JobRequestStatus;
import com.nhattung.wogo.enums.PaymentMethod;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.BookingRepository;
import com.nhattung.wogo.service.address.IAddressService;
import com.nhattung.wogo.service.bookingfile.IBookingFileService;
import com.nhattung.wogo.service.payment.IPaymentService;
import com.nhattung.wogo.service.service.IServiceService;
import com.nhattung.wogo.service.user.IUserService;
import com.nhattung.wogo.service.worker.IWorkerService;
import com.nhattung.wogo.utils.RedisKeyUtil;
import com.nhattung.wogo.utils.SecurityUtils;
import com.nhattung.wogo.utils.UploadToS3;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final IServiceService serviceService;
    private final UploadToS3 uploadToS3;
    private final BookingRepository bookingRepository;
    private final IUserService userService;
    private final IWorkerService workerService;
    private final IAddressService addressService;
    private final IBookingFileService bookingFileService;
    private final ModelMapper modelMapper;
    private final IPaymentService paymentService;
    private static final long JOB_EXPIRATION_MINUTES = 30;
    private static final double EARTH_RADIUS_KM = 6371.0;
        @Override
        public JobRequestResponseDTO createJob(FindServiceRequestDTO request, List<MultipartFile> files) {
            Long userId = SecurityUtils.getCurrentUserId();
            ServiceWG service = serviceService.getServiceByIdEntity(request.getServiceId());
            UserResponseDTO user = userService.getUserById(userId);

            List<UploadS3Response> uploadResponses = Optional.ofNullable(files)
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .map(uploadToS3::uploadFileToS3)
                    .filter(Objects::nonNull)
                    .toList();

            // build job request
            String code = generateJobRequestCode();
            JobRequestResponseDTO job = JobRequestResponseDTO.builder()
                    .jobRequestCode(code)
                    .serviceId(service.getId())
                    .serviceName(service.getServiceName())
                    .description(request.getDescription())
                    .bookingDate(request.getBookingDate())
                    .estimatedPriceLower(request.getEstimatedPriceLower())
                    .estimatedPriceHigher(request.getEstimatedPriceHigher())
                    .bookingAddress(request.getAddress())
                    .distance(0.0)
                    .files(uploadResponses)
                    .user(user)
                    .status(JobRequestStatus.PENDING)
                    .build();

            // 1) Lưu detail job
            String jobDetailKey = RedisKeyUtil.jobDetailKey(code);
            redisTemplate.opsForValue().set(jobDetailKey, job, JOB_EXPIRATION_MINUTES, TimeUnit.MINUTES);

            // 2) Ghi code job vào list theo service
            String serviceKey = RedisKeyUtil.jobListByServiceKey(service.getId());
            redisTemplate.opsForList().rightPush(serviceKey, code);
            redisTemplate.expire(serviceKey, JOB_EXPIRATION_MINUTES, TimeUnit.MINUTES);

            return job;

        }

    @Override
    public List<JobRequestResponseDTO> getListPendingJobsMatchWorker() {

        Long workerId = SecurityUtils.getCurrentUserId();

        List<Long> serviceIds = serviceService.getAllServicesOfWorker()
                .stream()
                .flatMap(dto -> {
                    List<ServiceResponseDTO> children = dto.getService().getChildServices();
                    if (children == null || children.isEmpty()) {
                        // Không có child → lấy luôn parent
                        return Stream.of(dto.getService().getParentService());
                    }
                    return children.stream();
                })
                .map(ServiceResponseDTO::getId)
                .toList();

        if (serviceIds.isEmpty()) {
            return Collections.emptyList();
        }

        Address addressWorker = addressService.findByUserId(workerId);

        return serviceIds.stream()
                .map(RedisKeyUtil::jobListByServiceKey)
                .map(key -> redisTemplate.opsForList().range(key, 0, -1))
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .map(code -> (JobRequestResponseDTO) redisTemplate.opsForValue()
                        .get(RedisKeyUtil.jobDetailKey((String) code)))
                .filter(Objects::nonNull)
                .filter(job -> JobRequestStatus.PENDING.equals(job.getStatus()))
                .peek(job -> {
                    Address addressCustomer = addressService.findByUserId(job.getUser().getId());

                    double distance = haversine(HaversineRequestDTO.builder()
                            .latCustomer(addressCustomer.getLatitude())
                            .lonCustomer(addressCustomer.getLongitude())
                            .latWorker(addressWorker.getLatitude())
                            .lonWorker(addressWorker.getLongitude())
                            .build());

                    job.setDistance(distance);
                })
                .toList();

    }
    @Override
    public boolean verifyJobRequest(JobRequestDTO request) {
        Long workerId = SecurityUtils.getCurrentUserId();
        String code = request.getJobRequestCode();
        String lockKey = RedisKeyUtil.jobLockKey(code);

        // lock (SETNX) để chỉ 1 worker nhận
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, workerId, Duration.ofSeconds(10));
        if (Boolean.FALSE.equals(locked)) {
            return false; // đã có người lock trước
        }

        try {
            JobRequestResponseDTO job = getJobAndValidate(code, JobRequestStatus.PENDING);
            job.setStatus(JobRequestStatus.ACCEPTED);
            job.setAcceptedBy(workerId);

            redisTemplate.opsForValue().set(RedisKeyUtil.jobDetailKey(code), job, JOB_EXPIRATION_MINUTES, TimeUnit.MINUTES);
            return true;
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    @Override
    public WorkerFoundResponseDTO sendQuote(JobRequestDTO request) {
        Long workerId = SecurityUtils.getCurrentUserId();

        Worker worker = workerService.getWorkerByUserId(workerId);

        return WorkerFoundResponseDTO.builder()
                .worker(WorkerResponseDTO.builder()
                        .id(worker.getId())
                        .averageRating(worker.getRatingAverage())
                        .totalJobs(worker.getTotalJobs())
                        .totalReviews(worker.getTotalReviews())
                        .description(worker.getDescription())
                        .build())
                .quotedPrice(request.getQuotedPrice())
                .build();
    }


    public JobRequestResponseDTO getJobByCode(String jobRequestCode) {
        return (JobRequestResponseDTO) redisTemplate.opsForValue().get(RedisKeyUtil.jobDetailKey(jobRequestCode));
    }

    @Override
    public BookingResponseDTO placeJob(PlaceJobRequestDTO request) {
        JobRequestResponseDTO job = getJobAndValidate(request.getJobRequestCode(), JobRequestStatus.ACCEPTED);
        ServiceWG service = serviceService.getServiceByIdEntity(job.getServiceId());

        // tạo booking
        Booking booking = saveBooking(BookingRequestDTO.builder()
                .userId(job.getUser().getId())
                .workerId(request.getWorkerId())
                .service(service)
                .bookingDate(LocalDateTime.now())
                .description(job.getDescription())
                .distanceKm(job.getDistance())
                .bookingAddress(job.getBookingAddress())
                .totalAmount(request.getQuotedPrice())
                .build());

        // lưu file
        bookingFileService.saveFiles(job.getFiles(), booking);

        // xoá job detail sau khi booking thành công
        redisTemplate.delete(RedisKeyUtil.jobDetailKey(request.getJobRequestCode()));

        return convertToBookingResponseDTO(booking);
    }

    @Override
    public void saveLocation(String bookingCode,RealtimeLocationDTO request) {
        String key = RedisKeyUtil.realtimeLocationKey(bookingCode);
        redisTemplate.opsForValue().set(key, request);
    }

    @Override
    public RealtimeLocationDTO getLocation(String bookingCode) {
        String key = RedisKeyUtil.realtimeLocationKey(bookingCode);
        return (RealtimeLocationDTO) redisTemplate.opsForValue().get(key);
    }

    @Override
    public void updateStatusBooking(UpdateStatusBookingRequestDTO request) {
        Booking booking = bookingRepository.findByBookingCode(request.getBookingCode())
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
        booking.setBookingStatus(request.getStatus());
        bookingRepository.save(booking);
    }

    @Override
    public double haversine(HaversineRequestDTO request) {
        double dLat = Math.toRadians(request.getLatWorker() - request.getLatCustomer());
        double dLon = Math.toRadians(request.getLonWorker() - request.getLonCustomer());
        double rLatCustomer = Math.toRadians(request.getLatCustomer());
        double rLatWorker = Math.toRadians(request.getLatWorker());

        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(rLatCustomer) * Math.cos(rLatWorker) * Math.pow(Math.sin(dLon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        return EARTH_RADIUS_KM * c; // km

    }

    @Override
    public TransactionResponseDTO createBookingTransaction(String bookingCode) {
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getTotalAmount() == null || booking.getTotalAmount().doubleValue() <= 0) {
            throw new AppException(ErrorCode.INVALID_BOOKING_AMOUNT);
        }


        String linkQR = generateQRLink(booking);

        return TransactionResponseDTO.builder()
                .linkTransaction(linkQR)
                .minDateTransaction(LocalDateTime.now())
                .maxDateTransaction(LocalDateTime.now().plusMinutes(5))
                .transactionStatus(false)
                .build();
    }

    @Override
    public BookingResponseDTO confirmPrice(ConfirmPriceRequestDTO request) {
        Booking booking = bookingRepository.findByBookingCode(request.getBookingCode())
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getBookingStatus() != BookingStatus.ARRIVED) {
            throw new AppException(ErrorCode.BOOKING_CANNOT_CONFIRM_PRICE);
        }

        BigDecimal platformFee = booking.getTotalAmount()
                .multiply(BigDecimal.valueOf(WogoConstants.PLATFORM_FEE_PERCENTAGE))
                .setScale(2, BigDecimal.ROUND_HALF_UP);

        booking.setPlatformFee(platformFee);
        booking.setTotalAmount(request.getFinalPrice());
        booking.setExtraServicesNotes(request.getNotes());
        booking.setBookingStatus(BookingStatus.NEGOTIATING);

        if(booking.getBookingPayment() == null){
            paymentService.savePayment(PaymentRequestDTO.builder()
                            .bookingCode(booking.getBookingCode())
                            .amount(booking.getTotalAmount())
                            .paymentMethod(PaymentMethod.BANK_TRANSFER)
                            .paidAt(null)
                    .build());
        }

        return convertToBookingResponseDTO(bookingRepository.save(booking));
    }

    private String generateQRLink(Booking booking) {
        try {
            return String.format("https://qr.sepay.vn/img?acc=%s&bank=%s&amount=%s&des=%s",
                    URLEncoder.encode(WogoConstants.ACCOUNT_NUMBER, StandardCharsets.UTF_8),
                    URLEncoder.encode(WogoConstants.BANK_NAME, StandardCharsets.UTF_8),
                    URLEncoder.encode(booking.getTotalAmount().toString(), StandardCharsets.UTF_8),
                    URLEncoder.encode("TTDV" + booking.getBookingCode(), StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            throw new AppException(ErrorCode.QR_LINK_GENERATION_FAILED);
        }
    }

    @Override
    public Booking saveBooking(BookingRequestDTO request) {
        User user = userService.getUserByIdEntity(request.getUserId());
        Worker worker = workerService.getWorkerByUserId(request.getWorkerId());
        return bookingRepository.save(createBooking(request, user, worker));
    }


    private JobRequestResponseDTO getJobAndValidate(String code, JobRequestStatus expected) {
        JobRequestResponseDTO job = getJobByCode(code);
        if (job == null || !expected.equals(job.getStatus())) {
            throw new IllegalStateException("Job invalid or not in expected status");
        }
        return job;
    }

    private Booking createBooking(BookingRequestDTO request,User user, Worker worker) {
        return Booking.builder()
                .bookingCode(generateBookingCode())
                .service(request.getService())
                .user(user)
                .worker(worker)
                .bookingDate(request.getBookingDate())
                .startDate(null)
                .endDate(null)
                .description(request.getDescription())
                .distanceKm(request.getDistanceKm())
                .bookingStatus(BookingStatus.COMING)
                .durationMinutes(0)
                .title(request.getService().getServiceName())
                .bookingAddress(request.getBookingAddress())
                .build();
    }

    private String generateBookingCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return "BK-" + uuid.substring(0, 8) + "-" + LocalDateTime.now().getYear();
    }

    private String generateJobRequestCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return "JR-" + uuid.substring(0, 8) + "-" + LocalDateTime.now().getYear();
    }

    private BookingResponseDTO convertToBookingResponseDTO(Booking booking) {
            BookingResponseDTO response = modelMapper.map(booking, BookingResponseDTO.class);
            List<BookingFileDTO> files = bookingFileService.getFilesByBookingId(booking.getId());
            response.setFiles(files);
            return response;
    }
}
