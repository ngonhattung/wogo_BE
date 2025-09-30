package com.nhattung.wogo.service.worker;

import com.nhattung.wogo.dto.request.UpdateWorkerRequestDTO;
import com.nhattung.wogo.dto.request.WorkerRequestDTO;
import com.nhattung.wogo.entity.Worker;

public interface IWorkerService {
    Worker saveWorker(WorkerRequestDTO request);
    Worker getWorkerByUserId(Long userId);
    boolean isWorkerExists(Long userId);
    void updateWorker(UpdateWorkerRequestDTO request,Worker worker);
}
