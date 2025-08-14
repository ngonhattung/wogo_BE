package com.nhattung.wogo.service.worker;

import com.nhattung.wogo.dto.request.WorkerRequestDTO;
import com.nhattung.wogo.dto.response.WorkerResponseDTO;
import com.nhattung.wogo.entity.Worker;
import com.nhattung.wogo.enums.WorkStatus;
import com.nhattung.wogo.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkerService implements IWorkerService{

    private final WorkerRepository workerRepository;
    private final ModelMapper modelMapper;
    @Override
    public WorkerResponseDTO saveWorker(WorkerRequestDTO request) {
        Worker worker = createWorker(request);

        return convertToResponseDTO(
                workerRepository.save(worker)
        );

    }

    @Override
    public Worker getWorkerByUserId(Long userId) {
        return workerRepository.findByUserId(userId)
                .orElse(null);
    }

    private Worker createWorker(WorkerRequestDTO request) {
        return Worker.builder()
                .user(request.getUser())
                .description("")
                .totalReviews(0)
                .totalJobs(0)
                .ratingAverage(0.0)
                .status(WorkStatus.BUSY)
                .build();
    }

    private WorkerResponseDTO convertToResponseDTO(Worker worker) {
        return modelMapper.map(worker, WorkerResponseDTO.class);
    }
}
