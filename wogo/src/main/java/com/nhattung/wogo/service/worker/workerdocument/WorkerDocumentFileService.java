package com.nhattung.wogo.service.worker.workerdocument;

import com.nhattung.wogo.dto.response.UploadS3Response;
import com.nhattung.wogo.entity.WorkerDocument;
import com.nhattung.wogo.entity.WorkerDocumentFile;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.WorkerDocumentFileRepository;
import com.nhattung.wogo.utils.UploadToS3;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
@RequiredArgsConstructor
public class WorkerDocumentFileService implements IWorkerDocumentFileService {

    private final WorkerDocumentFileRepository workerDocumentFileRepository;
    private final UploadToS3 uploadToS3;

    @Override
    public void saveWorkerDocumentFile(List<MultipartFile> files, WorkerDocument workerDocument) {
        for(MultipartFile file : files) {
                // Upload to S3
                UploadS3Response s3Response = uploadToS3.uploadFileToS3(file);

                // Save to database
                workerDocumentFileRepository.save(
                        WorkerDocumentFile.builder()
                                .workerDocument(workerDocument)
                                .fileName(s3Response.getFileName())
                                .fileType(s3Response.getFileType())
                                .fileUrl(s3Response.getFileUrl())
                                .build()
                );
        }

    }

    @Override
    public List<WorkerDocumentFile> getWorkerDocumentFilesByDocumentId(Long documentId) {
        return workerDocumentFileRepository.findByWorkerDocumentId(documentId)
                .orElseThrow(() -> new AppException(ErrorCode.WORKER_DOCUMENT_FILE_NOT_FOUND));
    }
}
