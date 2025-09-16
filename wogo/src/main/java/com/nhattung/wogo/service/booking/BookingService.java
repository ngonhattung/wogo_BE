package com.nhattung.wogo.service.booking;

import com.nhattung.wogo.dto.request.*;
import com.nhattung.wogo.dto.response.*;
import com.nhattung.wogo.entity.*;
import com.nhattung.wogo.enums.BookingStatus;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.enums.JobRequestStatus;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.BookingRepository;
import com.nhattung.wogo.service.address.IAddressService;
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
    private static final long JOB_EXPIRATION_MINUTES = 30;
    private final ModelMapper modelMapper;
    private static final double EARTH_RADIUS_KM = 6371.0;
    private final IAddressService addressService;
        @Override
        public JobRequestResponseDTO createJob(FindServiceRequestDTO request, List<MultipartFile> files) {
            Long userId = SecurityUtils.getCurrentUserId();
            ServiceWG service = serviceService.getServiceByIdEntity(request.getServiceId());
            UserResponseDTO user = userService.getUserById(userId);

            List<String> imageUrls = Optional.ofNullable(files)
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .map(uploadToS3::uploadFileToS3)
                    .filter(Objects::nonNull)
                    .map(UploadS3Response::getFileUrl)
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
                    .fileUrls(imageUrls)
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

        // xoá job detail sau khi booking thành công
        redisTemplate.delete(RedisKeyUtil.jobDetailKey(request.getJobRequestCode()));

        return convertToBookingResponseDTO(booking,job.getFileUrls());
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

    private BookingResponseDTO convertToBookingResponseDTO(Booking booking,List<String> fileUrls) {
        BookingResponseDTO responseDTO = modelMapper.map(booking, BookingResponseDTO.class);
        responseDTO.setFileUrls(fileUrls);
        return responseDTO;
    }
}
