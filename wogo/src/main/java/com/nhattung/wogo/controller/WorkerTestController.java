package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.WorkerVerificationTestResponseDTO;
import com.nhattung.wogo.service.workerverificationtest.IWorkerVerificationTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/worker-test")
@RequiredArgsConstructor
public class WorkerTestController {

    private final IWorkerVerificationTestService workerVerificationTestService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<PageResponse<WorkerVerificationTestResponseDTO>> getAllWorkerTests(@RequestParam(defaultValue = "1") int page,
                                                                                          @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<WorkerVerificationTestResponseDTO>>builder()
                .message("Get all worker tests successfully")
                .result(workerVerificationTestService.getAllWorkerVerificationTests(page, size))
                .build();
    }
}
