package com.nhattung.wogo.service.wokertest;

import com.nhattung.wogo.dto.request.SubmitTestRequestDTO;
import com.nhattung.wogo.dto.request.WorkerTestRequestDTO;
import com.nhattung.wogo.dto.response.CompleteTestResponseDTO;
import com.nhattung.wogo.dto.response.CreateTestResponseDTO;

public interface IWorkerTestService {
    CreateTestResponseDTO createWorkerTest(WorkerTestRequestDTO request);
    CompleteTestResponseDTO completeWorkerTest(SubmitTestRequestDTO request);

}
