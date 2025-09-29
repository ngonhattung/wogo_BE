package com.nhattung.wogo.service.worker.workverification;

import com.nhattung.wogo.dto.request.WorkerVerificationRequestDTO;
import com.nhattung.wogo.entity.WorkerVerification;
import com.nhattung.wogo.enums.VerificationType;

import java.util.List;

public interface IWorkerVerificationService {

    void saveWorkerVerification(WorkerVerificationRequestDTO request);
    void updateWorkerVerification(Long verificationId, WorkerVerificationRequestDTO request);
    WorkerVerification getWorkerVerificationByWorkerTestId(Long workerTestId);
    List<WorkerVerification> getWorkerVerificationByServiceIdAndUserIdAndType(Long serviceId, VerificationType type);

    WorkerVerification getWorkerVerificationByWorkerDocumentId(Long id);
}
