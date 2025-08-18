package com.nhattung.wogo.service.workerdocument;

import com.nhattung.wogo.dto.request.WorkerDocumentRequestDTO;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.WorkerDocumentResponseDTO;
import com.nhattung.wogo.entity.WorkerDocument;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IWorkerDocumentService {
    WorkerDocument saveWorkerDocument(WorkerDocumentRequestDTO request, List<MultipartFile> files);
    WorkerDocumentResponseDTO getWorkerDocumentByUserId(Long userId);
    WorkerDocumentResponseDTO getWorkerDocumentById(Long id);
    WorkerDocumentResponseDTO updateWorkerDocument(WorkerDocumentRequestDTO request);
    PageResponse<WorkerDocumentResponseDTO> getAllWorkerDocuments(int page, int size);
    WorkerDocumentResponseDTO convertToResponseDTO(WorkerDocument workerDocument);
}
