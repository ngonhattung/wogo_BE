package com.nhattung.wogo.service.wallet;

import com.nhattung.wogo.dto.request.WorkerWalletRevenueRequestDTO;
import com.nhattung.wogo.dto.response.WorkerWalletRevenueResponseDTO;
import com.nhattung.wogo.entity.WorkerWalletRevenue;

public interface IWorkerWalletRevenueService {
    void saveWorkerWalletRevenue(WorkerWalletRevenueRequestDTO request);
    boolean checkExistWalletByWorkerId(Long workerId);
    WorkerWalletRevenueResponseDTO getWalletByUserId();
    WorkerWalletRevenue updateWalletRevenue(WorkerWalletRevenueRequestDTO request);
}
