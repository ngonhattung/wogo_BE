package com.nhattung.wogo.service.workerverification;

import com.nhattung.wogo.dto.request.WorkerVerificationRequestDTO;
import com.nhattung.wogo.entity.WorkerVerification;

public interface IWorkerVerificationService {

    void saveWorkerVerification(WorkerVerificationRequestDTO request);
    void updateWorkerVerification(Long verificationId, WorkerVerificationRequestDTO request);
    WorkerVerification getWorkerVerificationByWorkerTestId(Long workerTestId);
    WorkerVerification getWorkerVerificationById(Long id);

    WorkerVerification getWorkerVerificationByWorkerDocumentId(Long id);
}
