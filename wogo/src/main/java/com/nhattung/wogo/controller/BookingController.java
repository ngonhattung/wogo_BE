package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.request.FindServiceRequestDTO;
import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.JobRequestResponseDTO;
import com.nhattung.wogo.service.booking.IBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final IBookingService bookingService;


    @PostMapping("/find-workers")
    public ApiResponse<Void> findWorkers(@Valid @ModelAttribute FindServiceRequestDTO request,
                                         @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        bookingService.findWorkers(request, files);
        return ApiResponse.<Void>builder()
                .message("Find workers request sent successfully")
                .build();
    }

    @GetMapping("/job-requests")
    public ApiResponse<List<JobRequestResponseDTO>> getJobRequestsByListServiceId(@RequestParam List<Long> serviceIds) {
        return ApiResponse.<List<JobRequestResponseDTO>>builder()
                .message("Fetched job requests successfully")
                .result(bookingService.getJobRequestsByListServiceId(serviceIds))
                .build();
    }

}
