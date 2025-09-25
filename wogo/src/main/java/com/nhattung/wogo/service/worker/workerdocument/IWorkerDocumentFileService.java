package com.nhattung.wogo.service.worker.workerdocument;

import com.nhattung.wogo.entity.WorkerDocument;
import com.nhattung.wogo.entity.WorkerDocumentFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IWorkerDocumentFileService {
    void saveWorkerDocumentFile(List<MultipartFile> files, WorkerDocument workerDocument);
    List<WorkerDocumentFile> getWorkerDocumentFilesByDocumentId(Long documentId);
}
