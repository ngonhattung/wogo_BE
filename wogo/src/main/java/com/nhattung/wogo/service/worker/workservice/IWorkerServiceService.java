package com.nhattung.wogo.service.worker.workservice;

import com.nhattung.wogo.dto.response.WorkerServiceResponseDTO;
import com.nhattung.wogo.entity.ServiceWG;
import com.nhattung.wogo.entity.Worker;
import com.nhattung.wogo.entity.WorkerService;

import java.util.List;

public interface IWorkerServiceService {
    void saveWorkerService(Worker worker, ServiceWG service);
    boolean checkWorkerServiceExists(Long workerId, Long serviceId);
    WorkerServiceResponseDTO convertToResponseDTO(WorkerService workerService);
    List<WorkerService> getWorkerServicesByUserId();
}
