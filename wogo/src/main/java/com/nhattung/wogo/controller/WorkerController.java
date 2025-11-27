package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.WorkerResponseDTO;
import com.nhattung.wogo.dto.response.WorkerServiceResponseDTO;
import com.nhattung.wogo.service.serviceWG.IServiceService;
import com.nhattung.wogo.service.worker.IWorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/workers")
public class WorkerController {

    private final IServiceService serviceService;
    private final IWorkerService workerService;

    @GetMapping("/services-of-worker")
    public ApiResponse<List<WorkerServiceResponseDTO>> getAllWorkerServicesByWorkerId() {
        return ApiResponse.<List<WorkerServiceResponseDTO>>builder()
                .message("Fetched all users successfully")
                .result(serviceService.getAllServicesOfWorker())
                .build();
    }

    @GetMapping("/all-workers")
    public ApiResponse<List<WorkerResponseDTO>> getAllWorkers() {
        return ApiResponse.<List<WorkerResponseDTO>>builder()
                .message("Fetched all workers successfully")
                .result(workerService.getAllWorker())
                .build();
    }

}
