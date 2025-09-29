package com.nhattung.wogo.service.wallet.revenue;

import com.nhattung.wogo.dto.request.UpdateWalletRequestDTO;
import com.nhattung.wogo.dto.request.WorkerWalletRevenueRequestDTO;
import com.nhattung.wogo.entity.WorkerWalletRevenue;
import com.nhattung.wogo.repository.WorkerWalletRevenueRepository;
import com.nhattung.wogo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkerWalletRevenueService implements IWorkerWalletRevenueService {

    private final WorkerWalletRevenueRepository workerWalletRevenueRepository;
    private final ModelMapper modelMapper;
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

    @Override
    public WorkerWalletRevenue getWalletByUserId() {
        return workerWalletRevenueRepository.getWalletByUserId(SecurityUtils.getCurrentUserId());
    }

    @Override
    public void updateWalletRevenue(UpdateWalletRequestDTO request) {
        WorkerWalletRevenue walletRevenue = workerWalletRevenueRepository.getWalletByUserId(SecurityUtils.getCurrentUserId());
        if(request.isAdd()){
            walletRevenue.setTotalRevenue(walletRevenue.getTotalRevenue().add(request.getAmount()));
            walletRevenue.setRevenueBalance(walletRevenue.getRevenueBalance().add(request.getAmount()));
        } else {
            walletRevenue.setTotalRevenue(walletRevenue.getTotalRevenue().subtract(request.getAmount()));
            walletRevenue.setRevenueBalance(walletRevenue.getRevenueBalance().subtract(request.getAmount()));
        }
        workerWalletRevenueRepository.save(walletRevenue);
    }

}
