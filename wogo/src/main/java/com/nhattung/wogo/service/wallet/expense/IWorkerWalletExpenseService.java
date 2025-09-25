package com.nhattung.wogo.service.wallet.expense;

import com.nhattung.wogo.dto.request.WorkerWalletExpenseRequestDTO;
import com.nhattung.wogo.dto.response.WorkerWalletExpenseResponseDTO;
import com.nhattung.wogo.entity.WorkerWalletExpense;

public interface IWorkerWalletExpenseService {
    void saveWorkerWalletExpense(WorkerWalletExpenseRequestDTO request);
    boolean checkExistWalletByWorkerId(Long workerId);
    WorkerWalletExpenseResponseDTO getWalletByUserId();
    WorkerWalletExpense updateWalletExpense(WorkerWalletExpenseRequestDTO request);
}
