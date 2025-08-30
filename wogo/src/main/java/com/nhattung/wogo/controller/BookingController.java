package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.request.AcceptJobRequestDTO;
import com.nhattung.wogo.dto.request.FindServiceRequestDTO;
import com.nhattung.wogo.dto.response.ApiResponse;
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
//    private final JobListener jobListener;
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

    @PostMapping("/accept-job")
    public ApiResponse<WorkerFoundResponseDTO> acceptJobRequest(@RequestBody AcceptJobRequestDTO request) {

        boolean isValid = bookingService.verifyJobRequest(request);

        if(isValid){
            JobRequestResponseDTO job = bookingService.getJobByCode(request.getJobRequestCode());

            messagingTemplate.convertAndSend(
                    "/topic/job-accepted/" + job.getServiceId(),
                    job
            );
            return ApiResponse.<WorkerFoundResponseDTO>builder()
                    .message("Job accepted successfully")
                    .result(bookingService.acceptJobRequest(request))
                    .build();
        }else {
            messagingTemplate.convertAndSendToUser(SecurityUtils.getCurrentUserId().toString(), "/queue/errors",
                    "Job is not available");
            return ApiResponse.<WorkerFoundResponseDTO>builder()
                    .message("Job request is no longer available")
                    .build();
        }

    }


}
