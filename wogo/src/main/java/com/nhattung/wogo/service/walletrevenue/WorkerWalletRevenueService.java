package com.nhattung.wogo.service.walletrevenue;

import com.nhattung.wogo.dto.request.WorkerWalletRevenueRequestDTO;
import com.nhattung.wogo.entity.WorkerWalletRevenue;
import com.nhattung.wogo.repository.WorkerWalletRevenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkerWalletRevenueService implements IWorkerWalletRevenue {

    private final WorkerWalletRevenueRepository workerWalletRevenueRepository;

    @Override
    public void saveWorkerWalletRevenue(WorkerWalletRevenueRequestDTO request) {
        WorkerWalletRevenue walletRevenue = WorkerWalletRevenue.builder()
                .totalRevenue(request.getTotalRevenue())
                .revenueBalance(request.getRevenueBalance())
                .isActive(true)
                .worker(request.getWorker())
                .build();
        workerWalletRevenueRepository.save(walletRevenue);
    }

    @Override
    public boolean checkExistWalletByWorkerId(Long workerId) {
        return workerWalletRevenueRepository.existsByWorkerId(workerId);
    }
}
