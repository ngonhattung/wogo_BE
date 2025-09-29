package com.nhattung.wogo.service.wallet.expense;

import com.nhattung.wogo.dto.request.UpdateWalletRequestDTO;
import com.nhattung.wogo.dto.request.WorkerWalletExpenseRequestDTO;
import com.nhattung.wogo.entity.WorkerWalletExpense;
import com.nhattung.wogo.repository.WorkerWalletExpenseRepository;
import com.nhattung.wogo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkerWalletExpenseService implements IWorkerWalletExpenseService {

    private final WorkerWalletExpenseRepository workerWalletExpenseRepository;
    private final ModelMapper modelMapper;

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

    @Override
    public WorkerWalletExpense getWalletByUserId() {
        return workerWalletExpenseRepository.getWalletByUserId(SecurityUtils.getCurrentUserId());
    }

    @Override
    public void updateWalletExpense(UpdateWalletRequestDTO request) {
        WorkerWalletExpense walletExpense = workerWalletExpenseRepository.getWalletByUserId(SecurityUtils.getCurrentUserId());

        if(request.isAdd())
        {
            walletExpense.setExpenseBalance(walletExpense.getExpenseBalance().add(request.getAmount()));
            walletExpense.setTotalExpense(walletExpense.getTotalExpense().add(request.getAmount()));
        } else {
            walletExpense.setExpenseBalance(walletExpense.getExpenseBalance().subtract(request.getAmount()));
            walletExpense.setTotalExpense(walletExpense.getTotalExpense().subtract(request.getAmount()));
        }

        workerWalletExpenseRepository.save(walletExpense);
    }

}
