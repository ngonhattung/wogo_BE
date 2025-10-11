package com.nhattung.wogo.service.booking;

import com.nhattung.wogo.constants.WogoConstants;
import com.nhattung.wogo.dto.request.*;
import com.nhattung.wogo.dto.request.SendQuoteRequestDTO;
import com.nhattung.wogo.dto.response.*;
import com.nhattung.wogo.entity.*;
import com.nhattung.wogo.enums.*;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.BookingHistoryRepository;
import com.nhattung.wogo.repository.BookingRepository;
import com.nhattung.wogo.service.booking.file.IBookingFileService;
import com.nhattung.wogo.service.notification.INotificationService;
import com.nhattung.wogo.service.user.address.IAddressService;
import com.nhattung.wogo.service.chat.room.IChatRoomService;
import com.nhattung.wogo.service.job.IJobService;
import com.nhattung.wogo.service.payment.IPaymentService;
import com.nhattung.wogo.service.sendquote.ISendQuoteService;
import com.nhattung.wogo.service.payment.sepay.SepayVerifyService;
import com.nhattung.wogo.service.serviceWG.IServiceService;
import com.nhattung.wogo.service.serviceWG.suggest.ISuggestService;
import com.nhattung.wogo.service.user.IUserService;
import com.nhattung.wogo.service.wallet.expense.IWorkerWalletExpenseService;
import com.nhattung.wogo.service.wallet.revenue.IWorkerWalletRevenueService;
import com.nhattung.wogo.service.wallet.transaction.IWalletTransactionService;
import com.nhattung.wogo.service.worker.IWorkerService;
import com.nhattung.wogo.utils.RedisKeyUtil;
import com.nhattung.wogo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService implements IBookingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final IServiceService serviceService;
    private final BookingRepository bookingRepository;
    private final IUserService userService;
    private final IWorkerService workerService;
    private final IAddressService addressService;
    private final IBookingFileService bookingFileService;
    private final ModelMapper modelMapper;
    private final IPaymentService paymentService;
    private final ISuggestService suggestService;
    private final IJobService jobService;
    private final ISendQuoteService sendQuoteService;
    private final IChatRoomService chatRoomService;
    private final IWorkerWalletRevenueService workerWalletRevenueService;
    private final IWorkerWalletExpenseService workerWalletExpenseService;
    private final IWalletTransactionService walletTransactionService;
    private final SepayVerifyService sepayVerifyService;
    private final BookingHistoryRepository historyRepository;
    private final INotificationService notificationService;

    @Override
    public JobResponseDTO createJob(FindServiceRequestDTO request, List<MultipartFile> files) {

        //Check xem serviceId co ton tai khong
        //Neu ton tai voi trang thai pending thi khong cho tao
        List<JobResponseDTO> existingJobs = jobService.getJobsByUserIdByStatus(JobRequestStatus.PENDING);
        boolean hasPendingJob = existingJobs.stream()
                .anyMatch(job -> job.getService().getId().equals(request.getServiceId()) && JobRequestStatus.PENDING.equals(job.getStatus()));
        if (hasPendingJob) {
            throw new AppException(ErrorCode.EXISTING_PENDING_JOB_REQUEST);
        }


        //Lưu vị trí của KHACHS hàng
        addressService.saveOrUpdateAddress(AddressRequestDTO.builder()
                .latitude(request.getLatitudeUser())
                .longitude(request.getLongitudeUser())
                .role(ROLE.CUSTOMER.name()).build());

        //Goi suggest de tinh gia
        EstimatedResponseDTO estimated = suggestService.suggestPrice(EstimatedPriceRequestDTO.builder()
                .serviceId(request.getServiceId())
                .distanceKm(WogoConstants.DEFAULT_DISTANCE_KM) // Thay bằng khoảng cách thực tế nếu có
                .build());


        JobResponseDTO jobResponseDTO = jobService.saveJob(CreateJobRequestDTO.builder()
                .serviceId(request.getServiceId())
                .description(request.getDescription())
                .address(request.getAddress())
                .bookingDate(request.getBookingDate() != null ? request.getBookingDate() : LocalDateTime.now().plusHours(1))
                .estimatedPriceLower(estimated.getEstimatedPriceLower())
                .estimatedPriceHigher(estimated.getEstimatedPriceHigher())
                .estimatedDurationMinutes(estimated.getEstimatedDurationMinutes())
                .longitudeUser(request.getLongitudeUser())
                .latitudeUser(request.getLatitudeUser())
                .build(), files);

        //Lưu thông báo
        notificationService.saveNotification(NotificationRequestDTO.builder()
                .title("Tình hình dịch vụ" + "#" + jobResponseDTO.getJobRequestCode())
                .description("Yêu cầu dịch vụ của bạn đã được tạo thành công và đang chờ thợ chấp nhận.")
                .type(NotificationType.SERVICE)
                .targetRole(ROLE.CUSTOMER)
                .build());

        return jobResponseDTO;
    }

    @Override
    public List<JobSummaryResponseDTO> getListPendingJobsMatchWorker() {

        //Check ví chi phí
        WorkerWalletExpense walletExpense = workerWalletExpenseService.getWalletByUserId();
        if (walletExpense.getExpenseBalance().doubleValue() < WogoConstants.MINIMUM_WALLET_BALANCE) {
            throw new AppException(ErrorCode.WALLET_BALANCE_NOT_ENOUGH);
        }

        Address addressWorker = addressService.findByWorkerId(SecurityUtils.getCurrentUserId());

        // Lấy tất cả serviceId mà thợ có thể làm
        List<Long> serviceIds = serviceService.getAllServicesOfWorker().stream().flatMap(dto -> {
            List<ServiceResponseDTO> children = dto.getService().getChildServices();
            return (children == null || children.isEmpty()) ? Stream.of(dto.getService().getParentService()) : children.stream();
        }).map(ServiceResponseDTO::getId).toList();

        if (serviceIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Lấy toàn bộ jobs theo danh sách serviceIds
        return serviceIds.stream()
                .flatMap(serviceId -> jobService.getJobsByServiceId(serviceId).stream())
                .peek(job -> setDistance(job, addressWorker)).toList();
    }

    @Override
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
    }

    @Override
    public BookingResponseDTO getBookingByBookingCode(String bookingCode) {
        return bookingRepository.findByBookingCode(bookingCode)
                .map(this::convertToBookingResponseDTO)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
    }

    // --- Method tách riêng tính khoảng cách ---
    private void setDistance(JobSummaryResponseDTO job, Address workerAddress) {
        double distance = haversine(HaversineRequestDTO.builder()
                .latCustomer(job.getLatitude())
                .lonCustomer(job.getLongitude())
                .latWorker(workerAddress.getLatitude())
                .lonWorker(workerAddress.getLongitude())
                .build());
        job.setDistance(distance);
    }

    @Override
    public boolean verifyJobRequest(SendQuoteRequestDTO request) {
        JobResponseDTO job = getJobAndValidate(request);
        return job != null;
    }

    private JobResponseDTO getJobAndValidate(SendQuoteRequestDTO request) {
        JobResponseDTO job = jobService.getJobByJobRequestCode(request.getJobRequestCode());

        if (!JobRequestStatus.PENDING.equals(job.getStatus())) {
            return null;
        }

        Long userId = SecurityUtils.getCurrentUserId();
        Worker currentWorker = workerService.getWorkerByUserId(userId);

        if (currentWorker == null) {
            throw new AppException(ErrorCode.WORKER_NOT_FOUND);
        }

        Long jobOwnerId = job.getUser() != null ? job.getUser().getId() : null;
        Long currentWorkerId = currentWorker.getId();

        // Kiểm tra nếu thợ đã gửi báo giá rồi
        List<SendQuotedResponseDTO> existingQuotes = job.getWorkerQuotes();

        if (existingQuotes != null) {
            boolean hasQuoted = existingQuotes
                    .stream()
                    .anyMatch(quote -> Objects.equals(quote.getWorker().getId(), currentWorkerId));
            if (hasQuoted) {
                throw new AppException(ErrorCode.YOU_ALREADY_SEND_QUOTE);
            }
        }

        if (Objects.equals(jobOwnerId, userId)) {
            throw new AppException(ErrorCode.CANNOT_ACCEPT_OWN_JOB);
        }

        return job;
    }

    @Override
    public WorkerQuoteResponseDTO sendQuote(SendQuoteRequestDTO request) {

        //Check xem thợ đã báo giá cho dịch vụ nay và trạng thái job còn pending hay không và có trong ngày không
        Job job = jobService.getJobByJobRequestCodeEntity(request.getJobRequestCode());
        LocalDateTime startOfDay = job.getBookingDate().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
        Long workerId = SecurityUtils.getCurrentUserId();

        boolean exists = sendQuoteService.checkExistSendQuote(job.getService().getId(), workerId, startOfDay, endOfDay);
        if (exists) {
            throw new AppException(ErrorCode.YOU_ALREADY_SEND_QUOTE);
        }

        //Tạo phòng chat
        chatRoomService.saveChatRoom(ChatRoomRequestDTO.builder()
                .jobRequestCode(generateCodeForChat(request.getJobRequestCode(), workerId, job.getUser().getId()))
                .lastMessageAt(Timestamp.valueOf(LocalDateTime.now()))
                .job(job)
                .isVisible(true)
                .build());

        //Lưu khoảng cách
        //Lưu vị trí của Thợ
        addressService.saveOrUpdateAddress(AddressRequestDTO.builder()
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .role(ROLE.WORKER.name())
                .build());

        double distance = haversine(HaversineRequestDTO.builder()
                .latCustomer(job.getLatitude())
                .lonCustomer(job.getLongitude())
                .latWorker(request.getLatitude())
                .lonWorker(request.getLongitude())
                .build());

        return sendQuoteService.saveSendQuote(CreateSendQuoteRequestDTO.builder()
                .job(job)
                .quotedPrice(request.getQuotedPrice())
                .distanceToJob(WogoConstants.ROAD_WAY * distance)
                .build());

    }


    private String generateCodeForChat(String jobRequestCode, Long workerId, Long userId) {
        return "job:" + jobRequestCode + ":worker:" + workerId + ":user:" + userId;
    }

    @Override
    @Transactional
    public BookingResponseDTO placeJob(PlaceJobRequestDTO request) {

        JobResponseDTO job = jobService.getJobByJobRequestCode(request.getJobRequestCode());

        // Update trạng thái job
        jobService.updateStatusAcceptJob(request.getJobRequestCode(), request.getWorkerId());

        // Lấy quote trực tiếp từ DB thay vì filter bằng stream
        WorkerQuoteResponseDTO quoted = sendQuoteService.getSendQuotesByJobRequestCode(request.getJobRequestCode())
                .stream()
                .filter(quote -> Objects.equals(quote.getWorker().getId(), request.getWorkerId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.WORKER_QUOTE_NOT_FOUND));

        // Tạo booking
        Booking booking = saveBooking(BookingRequestDTO
                .builder()
                .bookingCode(job.getJobRequestCode())
                .bookingDate(job.getBookingDate())
                .workerId(request.getWorkerId())
                .service(serviceService.getServiceByIdEntity(job.getService().getId()))
                .description(job.getDescription())
                .distanceKm(quoted.getDistanceToJob())
                .bookingAddress(job.getBookingAddress())
                .totalAmount(request.getQuotedPrice())
                .build());

        // Lưu file
        bookingFileService.saveFiles(job.getFiles(), booking);

        return convertToBookingResponseDTO(booking);
    }

    @Override
    public void saveLocation(String bookingCode, RealtimeLocationDTO request) {
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

        if (request.getPaymentMethod() != null && request.getStatus() == BookingStatus.COMPLETED) {
            handlePaymentAndWallet(booking, request);
        }

        if(request.getStatus() == BookingStatus.WORKING)
        {
            booking.setStartDate(LocalDateTime.now());
            bookingRepository.save(booking);
        }
        bookingRepository.save(booking);
    }

    private void handlePaymentAndWallet(Booking booking, UpdateStatusBookingRequestDTO request) {

        switch (request.getPaymentMethod()) {
            case CASH -> handleCashPayment(booking);
            case BANK_TRANSFER -> handleBankTransfer(booking);
            default -> { return; }
        }

        BookingPayment payment = paymentService.updatePaymentStatus(
                PaymentRequestDTO.builder()
                        .booking(booking)
                        .paymentMethod(request.getPaymentMethod())
                        .build()
        );

        walletTransactionService.processWalletTransaction(
                ProcessWalletTransactionRequestDTO.builder()
                        .transactionId(payment.getWalletTransaction().getId())
                        .status(PaymentStatus.COMPLETED)
                        .processedAt(LocalDateTime.now())
                        .build()
        );
    }

    private void handleCashPayment(Booking booking) {
        workerWalletExpenseService.updateWalletExpense(UpdateWalletRequestDTO.builder()
                .amount(booking.getPlatformFee())
                .isAdd(false)
                .build());
    }

    private void handleBankTransfer(Booking booking) {
        BigDecimal workerAmount = booking.getTotalAmount().subtract(booking.getPlatformFee());
        workerWalletRevenueService.updateWalletRevenue(UpdateWalletRequestDTO.builder()
                .amount(workerAmount)
                .isAdd(true)
                .build());
    }

    @Override
    public double haversine(HaversineRequestDTO request) {
        double lat1 = Math.toRadians(request.getLatCustomer());
        double lon1 = Math.toRadians(request.getLonCustomer());
        double lat2 = Math.toRadians(request.getLatWorker());
        double lon2 = Math.toRadians(request.getLonWorker());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dLon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        return WogoConstants.ROAD_WAY * WogoConstants.EARTH_RADIUS_KM * c;
    }



    @Override
    public TransactionResponseDTO createBookingTransaction(String bookingCode) {

        Booking booking = bookingRepository.findByBookingCode(bookingCode).orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getTotalAmount() == null || booking.getTotalAmount().doubleValue() <= 0) {
            throw new AppException(ErrorCode.INVALID_BOOKING_AMOUNT);
        }

        WorkerWalletRevenue walletRevenue = workerWalletRevenueService.getWalletByUserId();

        BigDecimal workerAmount = booking.getTotalAmount().subtract(booking.getPlatformFee());

        walletTransactionService.saveWalletTransaction(WalletTransactionRequestDTO.builder()
                .amount(booking.getTotalAmount())
                .transactionType(TransactionType.PAYMENT)
                .status(PaymentStatus.PENDING).description("Payment for booking: " + booking.getBookingCode())
                .payment(booking.getBookingPayment())
                .walletRevenue(walletRevenue)
                .balanceBefore(walletRevenue.getRevenueBalance())
                .balanceAfter(walletRevenue.getRevenueBalance().add(workerAmount))
                .build());

        String linkQR = sepayVerifyService.createQRCodeForPayment(booking);

        return TransactionResponseDTO.builder().linkTransaction(linkQR).transactionStatus(false).build();
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

        Booking bookingSaved = bookingRepository.save(booking);

        if (bookingSaved.getBookingPayment() == null) {
            paymentService.savePayment(PaymentRequestDTO.builder()
                    .booking(bookingSaved)
                    .amount(booking.getTotalAmount())
                    .build());
        }

        return convertToBookingResponseDTO(bookingRepository.save(bookingSaved));


    }

    @Override
    public List<BookingHistoryResponseDTO> getBookingHistory() {
        List<Object[]> results = historyRepository.findBookingHistoryByUser(SecurityUtils.getCurrentUserId());
        return results.stream()
                .map(r -> new BookingHistoryResponseDTO(
                        (String) r[0],
                        (String) r[1],
                        ((Timestamp) r[2]).toLocalDateTime(),
                        (String) r[3],
                        (String) r[4],
                        (String) r[5]
                ))
                .toList();
    }

    @Override
    public Booking saveBooking(BookingRequestDTO request) {
        User user = userService.getCurrentUser();
        Worker worker = workerService.getWorkerById(request.getWorkerId());
        return bookingRepository.save(createBooking(request, user, worker));
    }


    private Booking createBooking(BookingRequestDTO request, User user, Worker worker) {
        return Booking.builder()
                .bookingCode(request.getBookingCode())
                .service(request.getService())
                .user(user)
                .worker(worker)
                .bookingDate(request.getBookingDate())
                .startDate(null)
                .endDate(null)
                .description(request.getDescription())
                .distanceKm(request.getDistanceKm())
                .bookingStatus(BookingStatus.PENDING)
                .durationMinutes(0)
                .title(request.getService().getServiceName())
                .bookingAddress(request.getBookingAddress())
                .totalAmount(request.getTotalAmount())
                .build();
    }

//    private String generateBookingCode() {
//        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
//        return "BK-" + uuid.substring(0, 8) + "-" + LocalDateTime.now().getYear();
//    }


    private BookingResponseDTO convertToBookingResponseDTO(Booking booking) {
        BookingResponseDTO response = modelMapper.map(booking, BookingResponseDTO.class);
        List<BookingFileDTO> files = bookingFileService.getFilesByBookingId(booking.getId());
        response.setFiles(files);
        return response;
    }
}
