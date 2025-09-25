package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.WorkerServiceResponseDTO;
import com.nhattung.wogo.service.service.IServiceService;
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

    @GetMapping("/services-of-worker")
    public ApiResponse<List<WorkerServiceResponseDTO>> getAllWorkerServicesByWorkerId() {
        return ApiResponse.<List<WorkerServiceResponseDTO>>builder()
                .message("Fetched all users successfully")
                .result(serviceService.getAllServicesOfWorker())
                .build();
    }

    //Cần phải có api chỉ lấy cái child không

}
