package com.nhattung.wogo.service.worker;

import com.nhattung.wogo.dto.request.WorkerRequestDTO;
import com.nhattung.wogo.entity.Worker;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.enums.WorkStatus;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkerService implements IWorkerService {

    private final WorkerRepository workerRepository;
    @Override
    public Worker saveWorker(WorkerRequestDTO request) {
        Worker worker = createWorker(request);
        return workerRepository.save(worker);
    }

    @Override
    public Worker getWorkerByUserId(Long userId) {
        return workerRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.WORKER_NOT_FOUND));
    }

    @Override
    public boolean isWorkerExists(Long userId) {
        return workerRepository.existsByUserId(userId);
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

}
