package com.nhattung.wogo.service.workerservice;

import com.nhattung.wogo.dto.response.WorkerServiceResponseDTO;
import com.nhattung.wogo.entity.ServiceWG;
import com.nhattung.wogo.entity.Worker;

import java.util.List;

public interface IWorkerServiceService {
    void saveWorkerService(Worker worker, ServiceWG service);
    List<WorkerServiceResponseDTO> getAllWorkerServicesByWorkerId();
    boolean checkWorkerServiceExists(Long workerId, Long serviceId);
}
