package com.nhattung.wogo.service.walletrevenue;

import com.nhattung.wogo.dto.request.WorkerWalletRevenueRequestDTO;

public interface IWorkerWalletRevenue {
    void saveWorkerWalletRevenue(WorkerWalletRevenueRequestDTO request);
    boolean checkExistWalletByWorkerId(Long workerId);
}
