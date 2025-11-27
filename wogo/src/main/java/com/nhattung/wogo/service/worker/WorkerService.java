package com.nhattung.wogo.service.worker;

import com.nhattung.wogo.dto.request.UpdateWorkerRequestDTO;
import com.nhattung.wogo.dto.request.WorkerRequestDTO;
import com.nhattung.wogo.dto.response.WorkerResponseDTO;
import com.nhattung.wogo.dto.response.WorkerServiceResponseDTO;
import com.nhattung.wogo.entity.Worker;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.enums.WorkStatus;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkerService implements IWorkerService {

    private final WorkerRepository workerRepository;
    private final ModelMapper modelMapper;

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

    @Override
    public void updateWorker(UpdateWorkerRequestDTO request, Worker worker) {
        //Cong tong so review va bookings
        worker.setTotalReviews(worker.getTotalReviews() + 1);
        worker.setTotalBookings(worker.getTotalBookings() + 1);

        //tinh lai trung binh rating
        double totalRating = worker.getRatingAverage() * (worker.getTotalReviews() - 1) + request.getRating();
        worker.setRatingAverage(totalRating / worker.getTotalReviews());

        workerRepository.save(worker);

    }

    @Override
    public Worker getWorkerById(Long workerId) {
        return workerRepository.findById(workerId)
                .orElseThrow(() -> new AppException(ErrorCode.WORKER_NOT_FOUND));
    }

    @Override
    public List<WorkerResponseDTO> getAllWorker() {
        return workerRepository.findAll()
                .stream()
                .map(this::convertWorkerToDto)
                .toList();
    }

    @Override
    public long countTotalWorker() {
        return workerRepository.count();
    }

    private Worker createWorker(WorkerRequestDTO request) {
        return Worker.builder()
                .user(request.getUser())
                .description("")
                .totalReviews(0)
                .totalBookings(0)
                .ratingAverage(0.0)
                .status(WorkStatus.BUSY)
                .build();
    }

    private WorkerResponseDTO convertWorkerToDto(Worker worker) {
        return modelMapper.map(worker, WorkerResponseDTO.class);
    }

}
