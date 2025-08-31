package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.request.FindServiceRequestDTO;
import com.nhattung.wogo.dto.request.JobRequestDTO;
import com.nhattung.wogo.dto.request.PlaceJobRequestDTO;
import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.BookingResponseDTO;
import com.nhattung.wogo.dto.response.JobRequestResponseDTO;
import com.nhattung.wogo.dto.response.WorkerFoundResponseDTO;
import com.nhattung.wogo.service.booking.IBookingService;
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
    @PostMapping("/create-job")
    public ApiResponse<Void> findWorkers(@Valid @ModelAttribute FindServiceRequestDTO request,
                                         @RequestParam(value = "files", required = false) List<MultipartFile> files) {

        JobRequestResponseDTO job = bookingService.createJob(request, files);

        //Push realtime cho thợ đang subscribe
        messagingTemplate.convertAndSend(
                "/topic/new-job/" + job.getServiceId(),
                job
        );

        //Khách subscribe để nhận thông báo thợ đã nhận job

        return ApiResponse.<Void>builder()
                .message("Find workers request sent successfully")
                .build();
    }

    @GetMapping("/job-requests")
    public ApiResponse<List<JobRequestResponseDTO>> listJobs(
            @RequestParam List<Long> serviceIds) {

        return ApiResponse.<List<JobRequestResponseDTO>>builder()
                .message("Pending jobs retrieved successfully")
                .result(bookingService.listPendingJobsByServiceIds(serviceIds))
                .build();
    }

    @PostMapping("/send-quote")
    public ApiResponse<WorkerFoundResponseDTO> acceptJobRequest(@RequestBody JobRequestDTO request) {

        boolean isValid = bookingService.verifyJobRequest(request);

        if(isValid){
            JobRequestResponseDTO job = bookingService.getJobByCode(request.getJobRequestCode());

            //Gửi realtime cho khách hàng là job đã được gửi báo giá
            messagingTemplate.convertAndSend(
                    "/topic/send-quote/" + job.getServiceId(),
                    job
            );

            //Thợ subscribe để nhận thông báo chấp nhận hay từ chối

            return ApiResponse.<WorkerFoundResponseDTO>builder()
                    .message("Job accepted successfully")
                    .result(bookingService.sendQuote(request))
                    .build();
        }else {
            //subscribe ngay khi connect với socket
            messagingTemplate.convertAndSendToUser(SecurityUtils.getCurrentUserId().toString(), "/queue/errors",
                    "Job is not available");
            return ApiResponse.<WorkerFoundResponseDTO>builder()
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

        //Push realtime cho tất cả thợ là job đã bị huỷ (subscribe theo requestCode (để gửi cho tất cả thợ báo giá) ngay sau khi gửi quote )
        messagingTemplate.convertAndSend(
                "/topic/job-placed/" + request.getJobRequestCode(),
                "Job has been cancelled"
        );

        return ApiResponse.<Void>builder()
                .message("Cancel job successfully")
                .build();
    }


}
