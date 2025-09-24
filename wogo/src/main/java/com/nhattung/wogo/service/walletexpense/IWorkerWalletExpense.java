package com.nhattung.wogo.service.walletexpense;

import com.nhattung.wogo.dto.request.WorkerWalletExpenseRequestDTO;

public interface IWorkerWalletExpense {
    void saveWorkerWalletExpense(WorkerWalletExpenseRequestDTO request);
    boolean checkExistWalletByWorkerId(Long workerId);
}
