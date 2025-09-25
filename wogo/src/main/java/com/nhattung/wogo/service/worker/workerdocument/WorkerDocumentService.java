package com.nhattung.wogo.service.worker.workerdocument;

import com.nhattung.wogo.dto.request.WorkerDocumentRequestDTO;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.WorkerDocumentFileResponseDTO;
import com.nhattung.wogo.dto.response.WorkerDocumentResponseDTO;
import com.nhattung.wogo.entity.WorkerDocument;
import com.nhattung.wogo.entity.WorkerDocumentFile;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.WorkerDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkerDocumentService implements IWorkerDocumentService {

    private final WorkerDocumentRepository workerDocumentRepository;
    private final ModelMapper modelMapper;
    private final IWorkerDocumentFileService workerDocumentFileService;
    @Override
    public WorkerDocument saveWorkerDocument(WorkerDocumentRequestDTO request, List<MultipartFile> files) {
        WorkerDocument workerDocument = createWorkerDocument(request);
        workerDocumentFileService.saveWorkerDocumentFile(files, workerDocument);

        return workerDocumentRepository.save(workerDocument);
    }

    private WorkerDocument createWorkerDocument(WorkerDocumentRequestDTO request) {
        return WorkerDocument.builder()
                .documentType(request.getDocumentType())
                .documentName(request.getDocumentName())
                .verificationStatus(request.getVerificationStatus())
                .build();
    }


    @Override
    public WorkerDocumentResponseDTO getWorkerDocumentById(Long id) {
        return workerDocumentRepository.findById(id)
                .map(this::convertToResponseDTO)
                .orElseThrow(() -> new AppException(ErrorCode.WORKER_DOCUMENT_NOT_FOUND));
    }

    @Override
    public WorkerDocumentResponseDTO updateWorkerDocument(WorkerDocumentRequestDTO request) {
        return workerDocumentRepository.findById(request.getId())
                .map(existingDocument -> updateWorkerDocumentExisting(existingDocument, request))
                .map(workerDocumentRepository::save)
                .map(this::convertToResponseDTO)
                .orElseThrow(() -> new AppException(ErrorCode.WORKER_DOCUMENT_NOT_FOUND));
    }

    private WorkerDocument updateWorkerDocumentExisting(WorkerDocument existingDocument, WorkerDocumentRequestDTO request) {
        existingDocument.setVerificationStatus(request.getVerificationStatus());
        return existingDocument;
    }


    @Override
    public PageResponse<WorkerDocumentResponseDTO> getAllWorkerDocuments(int page, int size) {
        if(page < 0 || size <= 0) {
            throw new AppException(ErrorCode.INVALID_PAGE_SIZE);
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<WorkerDocument> workerDocuments = workerDocumentRepository.findAll(pageable);
        List<WorkerDocumentResponseDTO> responseList = workerDocuments.stream()
                .map(this::convertToResponseDTO)
                .toList();

        return PageResponse.<WorkerDocumentResponseDTO>builder()
                .currentPage(page)
                .totalPages(workerDocuments.getTotalPages())
                .totalElements(workerDocuments.getTotalElements())
                .pageSize(workerDocuments.getSize())
                .data(responseList)
                .build();
    }

    @Override
    public WorkerDocumentResponseDTO convertToResponseDTO(WorkerDocument workerDocument) {

        WorkerDocumentResponseDTO responseDTO = modelMapper.map(workerDocument, WorkerDocumentResponseDTO.class);

        List<WorkerDocumentFile> files = workerDocumentFileService
                .getWorkerDocumentFilesByDocumentId(workerDocument.getId());

        List<WorkerDocumentFileResponseDTO> fileResponseDTOS = files.stream()
                .map(file -> modelMapper.map(file, WorkerDocumentFileResponseDTO.class))
                .toList();

        responseDTO.setDocumentFiles(fileResponseDTOS);

        return responseDTO;

    }



}
