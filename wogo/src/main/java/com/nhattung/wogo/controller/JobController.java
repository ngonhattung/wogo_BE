package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.JobResponseDTO;
import com.nhattung.wogo.dto.response.WorkerQuoteResponseDTO;
import com.nhattung.wogo.service.job.IJobService;
import com.nhattung.wogo.service.sendquote.ISendQuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/jobs")
public class JobController {

    private final IJobService jobService;
    private final ISendQuoteService sendQuoteService;

    @GetMapping("/my-jobRequests")
    public ApiResponse<List<JobResponseDTO>> getMyJobRequests() {
        return ApiResponse.<List<JobResponseDTO>>builder()
                .message("User's job requests retrieved successfully")
                .result(jobService.getJobsByUserId())
                .build();
    }


    @GetMapping("/my-quotes")
    public ApiResponse<List<WorkerQuoteResponseDTO>> getMyQuotes() {
        return ApiResponse.<List<WorkerQuoteResponseDTO>>builder()
                .message("User's quotes retrieved successfully")
                .result(sendQuoteService.getSendQuotesByWorkerId())
                .build();
    }

}
