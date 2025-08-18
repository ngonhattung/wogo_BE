package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.request.WorkerDocumentRequestDTO;
import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.WorkerDocumentResponseDTO;
import com.nhattung.wogo.service.workerdocument.IWorkerDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/worker-document")
public class WorkerDocumentController {

    private final IWorkerDocumentService workerDocumentService;


    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<PageResponse<WorkerDocumentResponseDTO>> getAllWorkerDocuments(@RequestParam(defaultValue = "1") int page,
                                                                                      @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<WorkerDocumentResponseDTO>>builder()
                .message("Get all worker documents successfully")
                .result(workerDocumentService.getAllWorkerDocuments(page, size))
                .build();
    }

    @GetMapping("/by-user-id")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<WorkerDocumentResponseDTO> getWorkerDocumentByUserId(@RequestParam Long userId) {
        return ApiResponse.<WorkerDocumentResponseDTO>builder()
                .message("Get worker document by user ID successfully")
                .result(workerDocumentService.getWorkerDocumentByUserId(userId))
                .build();
    }

//    @PutMapping("/update")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ApiResponse<WorkerDocumentResponseDTO> updateWorkerDocument(@RequestBody WorkerDocumentRequestDTO request) {
//        return ApiResponse.<WorkerDocumentResponseDTO>builder()
//                .message("Update worker document successfully")
//                .result(workerDocumentService.updateWorkerDocument(request))
//                .build();
//    }
}
