package com.nhattung.wogo.service.workerverificationtest;

import com.nhattung.wogo.dto.request.WorkerVerificationTestRequestDTO;
import com.nhattung.wogo.entity.WorkerVerificationTest;

public interface IWorkerVerificationTestService {

    WorkerVerificationTest saveWorkerVerificationTest(WorkerVerificationTestRequestDTO request);
    void updateWorkerVerificationTest(Long testId, WorkerVerificationTestRequestDTO request);
    WorkerVerificationTest getWorkerVerificationTestById(Long testId);

}
