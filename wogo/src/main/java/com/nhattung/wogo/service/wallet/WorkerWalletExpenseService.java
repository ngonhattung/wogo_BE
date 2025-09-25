package com.nhattung.wogo.service.wallet;

import com.nhattung.wogo.dto.request.WorkerWalletExpenseRequestDTO;
import com.nhattung.wogo.dto.response.WorkerWalletExpenseResponseDTO;
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
    public WorkerWalletExpenseResponseDTO getWalletByUserId() {
        return convertToDTO(workerWalletExpenseRepository.getWalletByUserId(SecurityUtils.getCurrentUserId()));
    }

    @Override
    public WorkerWalletExpense updateWalletExpense(WorkerWalletExpenseRequestDTO request) {
        WorkerWalletExpense walletExpense = workerWalletExpenseRepository.getWalletByUserId(SecurityUtils.getCurrentUserId());
        walletExpense.setTotalExpense(request.getTotalExpense());
        walletExpense.setExpenseBalance(request.getExpenseBalance());
        return workerWalletExpenseRepository.save(walletExpense);
    }

    private WorkerWalletExpenseResponseDTO convertToDTO(WorkerWalletExpense walletExpense) {
        return modelMapper.map(walletExpense, WorkerWalletExpenseResponseDTO.class);
    }
}
