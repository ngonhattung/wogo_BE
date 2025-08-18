package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.request.SubmitTestRequestDTO;
import com.nhattung.wogo.dto.request.WorkerDocumentRequestDTO;
import com.nhattung.wogo.dto.request.WorkerTestRequestDTO;
import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.CompleteTestResponseDTO;
import com.nhattung.wogo.dto.response.CreateTestResponseDTO;
import com.nhattung.wogo.dto.response.WorkerDocumentResponseDTO;
import com.nhattung.wogo.entity.WorkerDocument;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.service.wokerverify.IWorkerVerifyService;
import com.nhattung.wogo.service.workerdocument.IWorkerDocumentService;
import com.nhattung.wogo.service.workerdocumentfile.IWorkerDocumentFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/worker-verify")
public class WorkerVerifyController {

    private final IWorkerVerifyService workerVerifyService;
    @PostMapping("/create-test")
    public ApiResponse<CreateTestResponseDTO> createWorkerTest(@RequestBody WorkerTestRequestDTO request) {
        return ApiResponse.<CreateTestResponseDTO>builder()
                .message("Create worker test successfully")
                .result(workerVerifyService.createWorkerTest(request))
                .build();
    }

    @PostMapping("/complete-test")
    public ApiResponse<CompleteTestResponseDTO> completeWorkerTest(@RequestBody SubmitTestRequestDTO request) {
        return ApiResponse.<CompleteTestResponseDTO>builder()
                .message("Complete worker test successfully")
                .result(workerVerifyService.completeWorkerTest(request))
                .build();
    }

    @PostMapping("/upload-worker-document")
    public ApiResponse<WorkerDocumentResponseDTO> uploadWorkerDocument(@ModelAttribute WorkerDocumentRequestDTO request,
                                                                       @RequestParam(value = "files") List<MultipartFile> files) {
        return ApiResponse.<WorkerDocumentResponseDTO>builder()
                .message("Upload worker document successfully")
                .result(workerVerifyService.uploadWorkerDocument(request, files))
                .build();
    }


    @PutMapping("/update-status-worker-document")
    public ApiResponse<WorkerDocumentResponseDTO> updateStatusWorkerDocument(@RequestBody WorkerDocumentRequestDTO request) {
       return ApiResponse.<WorkerDocumentResponseDTO>builder()
                .message("Update status worker document successfully")
                .result(workerVerifyService.updateVerifyStatusByDocument(request))
                .build();
    }

}
