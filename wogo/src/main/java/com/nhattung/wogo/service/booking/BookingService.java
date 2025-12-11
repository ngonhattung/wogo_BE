package com.nhattung.wogo.service.booking;

import com.nhattung.wogo.constants.WogoConstants;
import com.nhattung.wogo.dto.request.*;
import com.nhattung.wogo.dto.response.*;
import com.nhattung.wogo.entity.*;
import com.nhattung.wogo.enums.*;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.BookingHistoryRepository;
import com.nhattung.wogo.repository.BookingRepository;
import com.nhattung.wogo.service.booking.file.IBookingFileService;
import com.nhattung.wogo.service.booking.statushistory.IBookingStatusHistoryService;
import com.nhattung.wogo.service.chat.room.IChatRoomService;
import com.nhattung.wogo.service.job.IJobService;
import com.nhattung.wogo.service.notification.INotificationService;
import com.nhattung.wogo.service.payment.IPaymentService;
import com.nhattung.wogo.service.payment.sepay.SepayVerifyService;
import com.nhattung.wogo.service.sendquote.ISendQuoteService;
import com.nhattung.wogo.service.serviceWG.IServiceService;
import com.nhattung.wogo.service.user.IUserService;
import com.nhattung.wogo.service.user.address.IAddressService;
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
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
    private final IJobService jobService;
    private final ISendQuoteService sendQuoteService;
    private final IChatRoomService chatRoomService;
    private final IWorkerWalletRevenueService workerWalletRevenueService;
    private final IWorkerWalletExpenseService workerWalletExpenseService;
    private final IWalletTransactionService walletTransactionService;
    private final SepayVerifyService sepayVerifyService;
    private final BookingHistoryRepository historyRepository;
    private final INotificationService notificationService;
    private final IBookingStatusHistoryService bookingStatusHistoryService;

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

        JobResponseDTO jobResponseDTO = jobService.saveJob(CreateJobRequestDTO.builder()
                .serviceId(request.getServiceId())
                .description(request.getDescription())
                .address(request.getAddress())
                .bookingDate(request.getBookingDate() != null ? request.getBookingDate() : LocalDateTime.now().plusHours(1))
                .estimatedPriceLower(request.getEstimatedPriceLower())
                .estimatedPriceHigher(request.getEstimatedPriceHigher())
                .longitudeUser(request.getLongitudeUser())
                .latitudeUser(request.getLatitudeUser())
                .build(), files);

        //Lưu thông báo
        notificationService.saveNotification(NotificationRequestDTO.builder()
                .title("Tình hình dịch vụ" + "#" + jobResponseDTO.getJobRequestCode())
                .description("Yêu cầu dịch vụ của bạn đã được tạo thành công")
                .type(NotificationType.SERVICE)
                .targetRole(ROLE.CUSTOMER)
                .targetUserId(jobResponseDTO.getUser().getId())
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

        //lưu thông báo
        notificationService.saveNotification(NotificationRequestDTO.builder()
                .title("Báo giá đã được gửi")
                .description("Bạn đã gửi báo giá cho yêu cầu dịch vụ mã #" + job.getJobRequestCode() +
                        ". Vui lòng chờ phản hồi từ khách hàng.")
                .type(NotificationType.SERVICE)
                .targetRole(ROLE.WORKER)
                .targetUserId(workerId)
                .build());


        return sendQuoteService.saveSendQuote(CreateSendQuoteRequestDTO.builder()
                .job(job)
                .quotedPrice(request.getQuotedPrice())
                .distanceToJob(distance)
                .build());

    }

    @Override
    public BookingResponseDTO negotiatePrice(NegotiatePriceRequestDTO request) {
        Booking booking = bookingRepository.findByBookingCode(request.getBookingCode())
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getBookingStatus() == BookingStatus.ARRIVED) {
            booking.setBookingStatus(BookingStatus.NEGOTIATING);
            booking = bookingRepository.save(booking);
        }

        return convertToBookingResponseDTO(booking);
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
        Object locationObj = redisTemplate.opsForValue().get(key);

        if (locationObj == null) {
            return null;
        }

        return modelMapper.map(locationObj, RealtimeLocationDTO.class);
    }

    @Override
    public void updateStatusBooking(UpdateStatusBookingRequestDTO request) {
        Booking booking = bookingRepository.findByBookingCode(request.getBookingCode())
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        BookingStatus oldStatus = booking.getBookingStatus();
        BookingStatus newStatus = request.getStatus();

        // Xử lý thanh toán nếu hoàn thành
        if (newStatus == BookingStatus.COMPLETED && request.getPaymentMethod() != null) {
            handlePaymentAndWallet(booking, request);
        }

        // Ghi thời gian bắt đầu khi chuyển sang WORKING
        if (newStatus == BookingStatus.WORKING && booking.getStartDate() == null) {
            booking.setStartDate(LocalDateTime.now());
        }

        // Lưu lịch sử thay đổi trạng thái
        bookingStatusHistoryService.saveBookingStatusHistory(
                CreateBookingStatusHistoryRequestDTO.builder()
                        .booking(booking)
                        .oldStatus(oldStatus)
                        .newStatus(newStatus)
                        .changedByType(ActorType.WORKER)
                        .changedById(booking.getWorker().getId())
                        .reason(null)
                        .build()
        );

        // Cập nhật trạng thái cuối cùng và lưu 1 lần
        booking.setBookingStatus(newStatus);
        bookingRepository.save(booking);
    }

    @Override
    public BookingResponseDTO cancelBooking(CancelBookingRequestDTO request) {
        Booking booking = bookingRepository.findByBookingCode(request.getBookingCode())
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        BookingStatus oldStatus = booking.getBookingStatus();
        BookingStatus newStatus = BookingStatus.CANCELLED;

        // Xác định ai là người hủy
        Long changedById;
        if (request.getCanceller() == ActorType.WORKER) {
            changedById = booking.getWorker() != null ? booking.getWorker().getId() : null;
        } else { // USER / CUSTOMER
            changedById = booking.getUser() != null ? booking.getUser().getId() : null;
        }

        // Cập nhật booking
        booking.setCancelledBy(request.getCanceller());
        booking.setCancelReason(request.getReason());
        booking.setEndDate(LocalDateTime.now());
        booking.setBookingStatus(newStatus);

        // Lưu lịch sử trạng thái
        bookingStatusHistoryService.saveBookingStatusHistory(
                CreateBookingStatusHistoryRequestDTO.builder()
                        .booking(booking)
                        .oldStatus(oldStatus)
                        .newStatus(newStatus)
                        .changedByType(request.getCanceller())
                        .changedById(changedById)
                        .reason(request.getReason())
                        .build()
        );

        bookingRepository.save(booking);

        //Update thông báo

        return convertToBookingResponseDTO(booking);
    }

    private void handlePaymentAndWallet(Booking booking, UpdateStatusBookingRequestDTO request) {
        PaymentMethod method = request.getPaymentMethod();

        // 1. Payment logic
        if (method == PaymentMethod.CASH) {
            handleCashPayment(booking);
        } else if (method == PaymentMethod.BANK_TRANSFER) {
            handleBankTransfer(booking);
        } else {
            return;
        }
    }

    private void handleCashPayment(Booking booking) {
        workerWalletExpenseService.updateWalletExpense(UpdateWalletRequestDTO.builder()
                .amount(booking.getPlatformFee())
                .isAdd(false)
                .build());

        paymentService.savePayment(PaymentRequestDTO.builder()
                .bookingId(booking.getId())
                .paymentStatus(PaymentStatus.COMPLETED)
                .amount(booking.getTotalAmount())
                .paymentMethod(PaymentMethod.CASH)
                .build());
    }

    private void handleBankTransfer(Booking booking) {

        BigDecimal workerAmount = booking.getTotalAmount().subtract(booking.getPlatformFee());
        workerWalletRevenueService.updateWalletRevenue(UpdateWalletRequestDTO.builder()
                .amount(workerAmount)
                .isAdd(true)
                .workerId(booking.getWorker().getId())
                .build());

        Payment payment = paymentService.updatePaymentStatus(
                PaymentRequestDTO.builder()
                        .bookingId(booking.getId())
                        .paymentMethod(PaymentMethod.BANK_TRANSFER)
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

        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getTotalAmount() == null || booking.getTotalAmount().doubleValue() <= 0) {
            throw new AppException(ErrorCode.INVALID_BOOKING_AMOUNT);
        }

        WorkerWalletRevenue walletRevenue = workerWalletRevenueService.getWalletByUserId();

        BigDecimal workerAmount = booking.getTotalAmount()
                .subtract(booking.getPlatformFee());

        WalletTransaction walletTransaction =  walletTransactionService.saveWalletTransaction(WalletTransactionRequestDTO.builder()
                .amount(booking.getTotalAmount())
                .transactionType(TransactionType.PAYMENT)
                .status(PaymentStatus.PENDING)
                .description("Payment for booking: " + booking.getBookingCode())
                .walletRevenue(walletRevenue)
                .balanceBefore(walletRevenue.getRevenueBalance())
                .balanceAfter(walletRevenue.getRevenueBalance().add(workerAmount))
                .build());

        paymentService.savePayment(PaymentRequestDTO.builder()
                .bookingId(booking.getId())
                .amount(booking.getTotalAmount())
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .walletTransactionId(walletTransaction.getId())
                .build());

        String linkQR = sepayVerifyService.createQRCodeForPayment(booking);

        return TransactionResponseDTO.builder()
                .linkTransaction(linkQR)
                .transactionStatus(false)
                .build();
    }

    @Override
    public BookingResponseDTO confirmPrice(ConfirmPriceRequestDTO request) {
        // Lấy booking
        Booking booking = bookingRepository.findByBookingCode(request.getBookingCode())
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        // Nếu người dùng chưa chấp nhận điều khoản -> chỉ trả về booking hiện tại, không cập nhật gì
        if (!request.isAcceptTerms()) {
            return convertToBookingResponseDTO(booking);
        }

        // Kiểm tra trạng thái booking
        if (booking.getBookingStatus() != BookingStatus.NEGOTIATING) {
            throw new AppException(ErrorCode.BOOKING_CANNOT_CONFIRM_PRICE);
        }

        // Tính phí nền tảng
        BigDecimal platformFee = request.getFinalPrice()
                .multiply(BigDecimal.valueOf(WogoConstants.PLATFORM_FEE_PERCENTAGE))
                .setScale(2, RoundingMode.HALF_UP);

        booking.setPlatformFee(platformFee);
        booking.setTotalAmount(request.getFinalPrice());
        booking.setExtraServicesNotes(request.getNotes());

        // Lưu booking sau khi cập nhật
        Booking savedBooking = bookingRepository.save(booking);

        // Trả về thông tin booking sau khi cập nhật
        return convertToBookingResponseDTO(savedBooking);
    }

    @Override
    public List<BookingHistoryResponseDTO> getBookingHistory(boolean isWorker) {
        List<Object[]> results = historyRepository.findBookingHistoryByUser(SecurityUtils.getCurrentUserId(),isWorker);
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
                .title(request.getService().getServiceName())
                .bookingAddress(request.getBookingAddress())
                .totalAmount(request.getTotalAmount())
                .build();
    }

    private BookingResponseDTO convertToBookingResponseDTO(Booking booking) {
        BookingResponseDTO response = modelMapper.map(booking, BookingResponseDTO.class);

        // Set files
        List<BookingFileDTO> files = bookingFileService.getFilesByBookingId(booking.getId());
        response.setFiles(files);

        // Set bookingPayment thủ công
        if (booking.getBookingPayment() != null) {
            Payment p = booking.getBookingPayment();
            PaymentResponseDTO paymentDTO = modelMapper.map(p, PaymentResponseDTO.class);

            response.setBookingPayment(paymentDTO);
        }

        return response;
    }


}
