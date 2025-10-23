package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.request.*;
import com.nhattung.wogo.dto.request.SendQuoteRequestDTO;
import com.nhattung.wogo.dto.response.*;
import com.nhattung.wogo.enums.BookingStatus;
import com.nhattung.wogo.service.booking.IBookingService;
import com.nhattung.wogo.service.job.JobService;
import com.nhattung.wogo.service.payment.sepay.ISepayVerifyService;
import com.nhattung.wogo.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final IBookingService bookingService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ISepayVerifyService sepayVerifyService;
    private final JobService jobService;


    @GetMapping("/getByCode/{bookingCode}")
    public ApiResponse<BookingResponseDTO> getJobByJobRequestCode(@PathVariable String bookingCode) {
        return ApiResponse.<BookingResponseDTO>builder()
                .message("Job request retrieved successfully")
                .result(bookingService.getBookingByBookingCode(bookingCode))
                .build();
    }

    @PostMapping("/create-job")
    public ApiResponse<JobResponseDTO> findWorkers(@Valid @ModelAttribute FindServiceRequestDTO request,
                                         @RequestParam(value = "files", required = false) List<MultipartFile> files) {

        JobResponseDTO job = bookingService.createJob(request, files);

        //FE tính khoảng cách trước khi hiện thị
        //Push realtime cho thợ đang subscribe
        messagingTemplate.convertAndSend(
                "/topic/new-job/" + job.getService().getId(),
                job
        );

        //Khách subscribe để nhận thông báo thợ đã nhận job (/topic/send-quote/" + job.getServiceId(),)

        return ApiResponse.<JobResponseDTO>builder()
                .message("Find workers request sent successfully")
                .result(job)
                .build();
    }


    @GetMapping("/job-available")
    public ApiResponse<List<JobSummaryResponseDTO>> listJobs() {
        return ApiResponse.<List<JobSummaryResponseDTO>>builder()
                .message("Pending jobs retrieved successfully")
                .result(bookingService.getListPendingJobsMatchWorker())
                .build();
    }

    @PostMapping("/send-quote")
    public ApiResponse<WorkerQuoteResponseDTO> sendQuote(@RequestBody SendQuoteRequestDTO request) {


        Long workerId = SecurityUtils.getCurrentUserId();

        //Lưu thêm địa chỉ thợ cho trường hợp realtime

        boolean isValid = bookingService.verifyJobRequest(request);

        if(isValid){
            JobResponseDTO job = jobService.getJobByJobRequestCode(request.getJobRequestCode());

            //Gửi realtime cho khách hàng là job đã được gửi báo giá
            messagingTemplate.convertAndSend(
                    "/topic/send-quote/" + job.getService().getId(),
                    job
            );

            //Thợ subscribe để nhận thông báo chấp nhận hay từ chối (/topic/job-placed/ + requestCode)

            return ApiResponse.<WorkerQuoteResponseDTO>builder()
                    .message("Job accepted successfully")
                    .result(bookingService.sendQuote(request))
                    .build();
        }else {
            //subscribe ngay khi connect với socket
            messagingTemplate.convertAndSendToUser(workerId.toString(), "/queue/errors",
                    "Job is not available");
            return ApiResponse.<WorkerQuoteResponseDTO>builder()
                    .message("Job request is no longer available")
                    .build();
        }

    }


    @PostMapping("/place-job")
    public ApiResponse<BookingResponseDTO> placeJob(@RequestBody PlaceJobRequestDTO request) {

        BookingResponseDTO booking = bookingService.placeJob(request);

        //Push realtime cho thợ đã được chọn (subscribe theo workerId để gửi riêng cho thợ đó)
        messagingTemplate.convertAndSend(
                "/topic/place-job/" + booking.getWorker().getId(),
                booking
        );


        //Push realtime cho các thợ khác là job đã được đặt (subscribe theo requestCode (để gửi cho tất cả thợ báo giá) ngay sau khi gửi quote )
        messagingTemplate.convertAndSend(
                "/topic/job-placed/" + request.getJobRequestCode(),
                booking // so sánh workerId để biết thợ nào được chọn
        );

        return ApiResponse.<BookingResponseDTO>builder()
                .message("Place job successfully")
                .result(booking)
                .build();
    }

    @PostMapping("/cancel-job")
    public ApiResponse<Void> cancelJob(@RequestBody PlaceJobRequestDTO request) {

        //Sau 2p nếu khách không quyết định tự cancel
        //Push realtime cho tất cả thợ là job đã bị huỷ (subscribe theo requestCode (để gửi cho tất cả thợ báo giá) ngay sau khi gửi quote )
        messagingTemplate.convertAndSend(
                "/topic/job-placed/" + request.getJobRequestCode(),
                "Job has been cancelled"
        );


        return ApiResponse.<Void>builder()
                .message("Cancel job successfully")
                .build();
    }

    // Driver gửi GPS mỗi 3s
    @PostMapping("/send-location/{bookingCode}")
    public ApiResponse<Void> sendLocation(@PathVariable String bookingCode,
                                          @RequestBody @Valid RealtimeLocationDTO request) {

        request.setUpdatedAt(System.currentTimeMillis());
        bookingService.saveLocation(bookingCode, request);

        //Push realtime cho khách hàng (subscribe theo bookingCode)
        messagingTemplate.convertAndSend(
                "/topic/driverLocation/" + bookingCode, request
        );

        return ApiResponse.<Void>builder()
                .message("Location sent successfully")
                .build();
    }

    @GetMapping("/get-location/{bookingCode}")
    public ApiResponse<RealtimeLocationDTO> getLocation(@PathVariable String bookingCode) {
        return ApiResponse.<RealtimeLocationDTO>builder()
                .message("Location retrieved successfully")
                .result(bookingService.getLocation(bookingCode))
                .build();
    }

    @PutMapping("/updateStatus")
    public ApiResponse<Void> updateStatus(@RequestBody UpdateStatusBookingRequestDTO request) {

        bookingService.updateStatusBooking(request);

        //Push realtime status cho khách hàng và worker (subscribe theo bookingCode)
        messagingTemplate.convertAndSend(
                "/topic/bookingStatus/" + request.getBookingCode(), request.getStatus()
        );

        return ApiResponse.<Void>builder()
                .message("Status updated successfully")
                .build();
    }


    @PostMapping("/confirm-price")
    public ApiResponse<BookingResponseDTO> confirmPrice(@RequestBody ConfirmPriceRequestDTO request) {

        BookingResponseDTO booking = bookingService.confirmPrice(request);

        //Push realtime cho khách hàng
        messagingTemplate.convertAndSend(
                "/topic/confirmPrice/" + request.getBookingCode(), request
        );

        return ApiResponse.<BookingResponseDTO>builder()
                .message("Confirm price successfully")
                .result(booking)
                .build();
    }

    @PostMapping("/create-payment/{bookingCode}")
    public ApiResponse<TransactionResponseDTO> createPayment(@PathVariable String bookingCode) {
        return ApiResponse.<TransactionResponseDTO>builder()
                .message("Create payment successfully")
                .result(bookingService.createBookingTransaction(bookingCode))
                .build();
    }

    //FE call mỗi 3s một lần để check trạng thái thanh toán
    @PostMapping("/verify-payment/{bookingCode}")
    public ApiResponse<Boolean> verifyPayment(@PathVariable String bookingCode) {
        boolean isPaid = sepayVerifyService.checkTransactionForPayment(bookingCode);

        if(isPaid){

            //Cập nhật trạng thái đã thanh toán
            bookingService.updateStatusBooking(
                    UpdateStatusBookingRequestDTO.builder()
                            .bookingCode(bookingCode)
                            .status(BookingStatus.PAID)
                            .build()
            );


            //Push realtime status cho khách hàng và worker (subscribe theo bookingCode)
            messagingTemplate.convertAndSend(
                    "/topic/bookingStatus/" + bookingCode, BookingStatus.PAID
            );
        }
        return ApiResponse.<Boolean>builder()
                .message(isPaid ? "Payment verified successfully" : "Payment verification failed")
                .result(isPaid)
                .build();
    }

    @GetMapping("/history")
    public ApiResponse<List<BookingHistoryResponseDTO>> getBookingHistory() {
        return ApiResponse.<List<BookingHistoryResponseDTO>>builder()
                .message("Booking history retrieved successfully")
                .result(bookingService.getBookingHistory())
                .build();
    }

}
