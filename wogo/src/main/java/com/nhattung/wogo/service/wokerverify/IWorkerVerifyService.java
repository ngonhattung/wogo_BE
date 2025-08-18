package com.nhattung.wogo.service.wokerverify;

import com.nhattung.wogo.dto.request.SubmitTestRequestDTO;
import com.nhattung.wogo.dto.request.WorkerDocumentRequestDTO;
import com.nhattung.wogo.dto.request.WorkerTestRequestDTO;
import com.nhattung.wogo.dto.response.CompleteTestResponseDTO;
import com.nhattung.wogo.dto.response.CreateTestResponseDTO;
import com.nhattung.wogo.dto.response.WorkerDocumentResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IWorkerVerifyService {
    CreateTestResponseDTO createWorkerTest(WorkerTestRequestDTO request);
    CompleteTestResponseDTO completeWorkerTest(SubmitTestRequestDTO request);
    WorkerDocumentResponseDTO uploadWorkerDocument(WorkerDocumentRequestDTO request, List<MultipartFile> files);
    WorkerDocumentResponseDTO updateVerifyStatusByDocument(WorkerDocumentRequestDTO request);

}
