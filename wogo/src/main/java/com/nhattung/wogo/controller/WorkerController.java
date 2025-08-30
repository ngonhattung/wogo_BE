package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.WorkerServiceResponseDTO;
import com.nhattung.wogo.service.workerservice.IWorkerServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/workers")
public class WorkerController {

    private final IWorkerServiceService workerServiceService;

    @GetMapping("/services-of-worker")
    public ApiResponse<List<WorkerServiceResponseDTO>> getAllWorkerServicesByWorkerId() {
        return ApiResponse.<List<WorkerServiceResponseDTO>>builder()
                .message("Fetched all users successfully")
                .result(workerServiceService.getAllWorkerServicesByWorkerId())
                .build();
    }

}
