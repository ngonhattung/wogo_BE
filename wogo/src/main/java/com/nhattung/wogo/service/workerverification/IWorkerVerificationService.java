package com.nhattung.wogo.service.workerverification;

import com.nhattung.wogo.dto.request.WorkerVerificationRequestDTO;
import com.nhattung.wogo.dto.response.WorkerVerificationResponseDTO;
import com.nhattung.wogo.entity.WorkerVerification;

public interface IWorkerVerificationService {

    WorkerVerificationResponseDTO saveWorkerVerification(WorkerVerificationRequestDTO request);
    WorkerVerificationResponseDTO updateWorkerVerification(Long verificationId, WorkerVerificationRequestDTO request);
    WorkerVerification getWorkerVerificationByWorkerTestId(Long workerTestId);
}
