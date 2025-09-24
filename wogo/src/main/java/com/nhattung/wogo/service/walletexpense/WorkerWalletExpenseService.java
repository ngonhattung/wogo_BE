package com.nhattung.wogo.service.walletexpense;

import com.nhattung.wogo.dto.request.WorkerWalletExpenseRequestDTO;
import com.nhattung.wogo.entity.WorkerWalletExpense;
import com.nhattung.wogo.repository.WorkerWalletExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkerWalletExpenseService implements IWorkerWalletExpense {

    private final WorkerWalletExpenseRepository workerWalletExpenseRepository;

    @Override
    public void saveWorkerWalletExpense(WorkerWalletExpenseRequestDTO request) {
        WorkerWalletExpense walletExpense = WorkerWalletExpense.builder()
                .totalExpense(request.getTotalExpense())
                .expenseBalance(request.getExpenseBalance())
                .isActive(true)
                .worker(request.getWorker())
                .build();
        workerWalletExpenseRepository.save(walletExpense);
    }

    @Override
    public boolean checkExistWalletByWorkerId(Long workerId) {
        return workerWalletExpenseRepository.existsByWorkerId(workerId);
    }
}
