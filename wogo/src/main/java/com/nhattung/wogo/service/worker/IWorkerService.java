package com.nhattung.wogo.service.worker;

import com.nhattung.wogo.dto.request.UpdateWorkerRequestDTO;
import com.nhattung.wogo.dto.request.WorkerRequestDTO;
import com.nhattung.wogo.dto.response.WorkerResponseDTO;
import com.nhattung.wogo.dto.response.WorkerServiceResponseDTO;
import com.nhattung.wogo.entity.Worker;

import java.util.List;

public interface IWorkerService {
    Worker saveWorker(WorkerRequestDTO request);
    Worker getWorkerByUserId(Long userId);
    boolean isWorkerExists(Long userId);
    void updateWorker(UpdateWorkerRequestDTO request,Worker worker);
    Worker getWorkerById(Long workerId);
    List<WorkerResponseDTO> getAllWorker();
    long countTotalWorker();
}
