package com.nhattung.wogo.service.workerservice;

import com.nhattung.wogo.dto.response.WorkerServiceResponseDTO;
import com.nhattung.wogo.entity.ServiceWG;
import com.nhattung.wogo.entity.Worker;
import com.nhattung.wogo.entity.WorkerService;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.WorkerServiceRepository;
import com.nhattung.wogo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkerServiceService implements IWorkerServiceService{

    private final WorkerServiceRepository workerServiceRepository;
    private final ModelMapper modelMapper;
    @Override
    public void saveWorkerService(Worker worker, ServiceWG service) {
        WorkerService workerService = createWorkerService(worker,service);
        workerServiceRepository.save(workerService);
    }

    private WorkerService createWorkerService(Worker worker, ServiceWG service) {
        return WorkerService.builder()
                .worker(worker)
                .service(service)
                .totalOrders(0)
                .totalRevenue(BigDecimal.valueOf(0.0))
                .isActive(true)
                .build();
    }

    @Override
    public List<WorkerServiceResponseDTO> getAllWorkerServicesByWorkerId() {
        return workerServiceRepository.findByUserId(SecurityUtils.getCurrentUserId())
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    public boolean checkWorkerServiceExists(Long workerId, Long serviceId) {
        return workerServiceRepository.existsByWorkerIdAndServiceId(workerId, serviceId);
    }

    private WorkerServiceResponseDTO convertToResponseDTO(WorkerService workerService) {
        return modelMapper.map(workerService, WorkerServiceResponseDTO.class);
    }
}
