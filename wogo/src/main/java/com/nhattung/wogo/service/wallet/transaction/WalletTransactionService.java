package com.nhattung.wogo.service.wallet.transaction;

import com.nhattung.wogo.dto.request.ProcessWalletTransactionRequestDTO;
import com.nhattung.wogo.dto.request.WalletTransactionRequestDTO;
import com.nhattung.wogo.dto.response.WalletTransactionResponseDTO;
import com.nhattung.wogo.entity.WalletTransaction;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletTransactionService implements IWalletTransactionService {

    private final WalletTransactionRepository walletTransactionRepository;

    @Override
    public WalletTransaction saveWalletTransaction(WalletTransactionRequestDTO request) {
        WalletTransaction walletTransaction = WalletTransaction.builder()
                .amount(request.getAmount())
                .transactionType(request.getTransactionType())
                .paymentStatus(request.getStatus())
                .description(request.getDescription())
                .withdrawal(request.getWithdrawal())
                .deposit(request.getDeposit())
                .transactionCode(UUID.randomUUID().toString())
                .walletRevenue(request.getWalletRevenue())
                .walletExpense(request.getWalletExpense())
                .beforeBalance(String.valueOf(request.getBalanceBefore()))
                .afterBalance(String.valueOf(request.getBalanceAfter()))
                .processedAt(request.getProcessedAt())
                .build();
        return walletTransactionRepository.save(walletTransaction);
    }

    @Override
    public void processWalletTransaction(ProcessWalletTransactionRequestDTO request) {
        WalletTransaction walletTransaction = walletTransactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_TRANSACTION_NOT_FOUND));

        walletTransaction.setPaymentStatus(request.getStatus());
        walletTransaction.setProcessedAt(request.getProcessedAt());

        walletTransactionRepository.save(walletTransaction);
    }

    @Override
    public List<WalletTransactionResponseDTO> getHistoryWithdrawalTransactions(Long workerId) {
        return walletTransactionRepository.getHistoryWithdrawalTransactions(workerId);
    }

    @Override
    public List<WalletTransactionResponseDTO> getHistoryDepositTransactions(Long workerId) {
        return walletTransactionRepository.getHistoryDepositTransactions(workerId);
    }
}
