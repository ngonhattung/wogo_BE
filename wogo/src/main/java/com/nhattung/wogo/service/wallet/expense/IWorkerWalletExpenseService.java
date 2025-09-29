package com.nhattung.wogo.service.wallet.expense;

import com.nhattung.wogo.dto.request.UpdateWalletRequestDTO;
import com.nhattung.wogo.dto.request.WorkerWalletExpenseRequestDTO;
import com.nhattung.wogo.entity.WorkerWalletExpense;

public interface IWorkerWalletExpenseService {
    void saveWorkerWalletExpense(WorkerWalletExpenseRequestDTO request);
    boolean checkExistWalletByWorkerId(Long workerId);
    WorkerWalletExpense getWalletByUserId();
    void updateWalletExpense(UpdateWalletRequestDTO request);
}
