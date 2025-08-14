package com.nhattung.wogo.service.worker;

import com.nhattung.wogo.dto.request.WorkerRequestDTO;
import com.nhattung.wogo.dto.response.WorkerResponseDTO;
import com.nhattung.wogo.entity.Worker;

public interface IWorkerService {
    WorkerResponseDTO saveWorker(WorkerRequestDTO request);
    Worker getWorkerByUserId(Long userId);
}
