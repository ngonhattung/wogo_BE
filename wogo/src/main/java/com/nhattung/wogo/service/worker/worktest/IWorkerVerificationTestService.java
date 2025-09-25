package com.nhattung.wogo.service.worker.worktest;

import com.nhattung.wogo.dto.request.WorkerVerificationTestRequestDTO;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.WorkerVerificationTestResponseDTO;
import com.nhattung.wogo.entity.WorkerVerificationTest;

public interface IWorkerVerificationTestService {

    WorkerVerificationTest saveWorkerVerificationTest(WorkerVerificationTestRequestDTO request);
    void updateWorkerVerificationTest(Long testId, WorkerVerificationTestRequestDTO request);
    WorkerVerificationTest getWorkerVerificationTestById(Long testId);
    PageResponse<WorkerVerificationTestResponseDTO> getAllWorkerVerificationTests(int page, int size);

}
