package com.nhattung.wogo.service.wallet.revenue;

import com.nhattung.wogo.dto.request.UpdateWalletRequestDTO;
import com.nhattung.wogo.dto.request.WorkerWalletRevenueRequestDTO;
import com.nhattung.wogo.entity.WorkerWalletRevenue;

public interface IWorkerWalletRevenueService {
    void saveWorkerWalletRevenue(WorkerWalletRevenueRequestDTO request);
    boolean checkExistWalletByWorkerId(Long workerId);
    WorkerWalletRevenue getWalletByUserId();
    void updateWalletRevenue(UpdateWalletRequestDTO request);
}
