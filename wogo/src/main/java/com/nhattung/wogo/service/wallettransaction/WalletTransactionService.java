package com.nhattung.wogo.service.wallettransaction;

import com.nhattung.wogo.dto.request.WalletTransactionRequestDTO;
import com.nhattung.wogo.entity.WalletTransaction;
import com.nhattung.wogo.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletTransactionService implements IWalletTransactionService {

    private final WalletTransactionRepository walletTransactionRepository;

    @Override
    public void saveWalletTransaction(WalletTransactionRequestDTO request) {
        WalletTransaction walletTransaction = WalletTransaction.builder()
                .amount(request.getAmount())
                .transactionType(request.getTransactionType())
                .paymentStatus(request.getStatus())
                .description(request.getDescription())
                .booking(request.getBooking())
                .withdrawalRequest(request.getWithdrawalRequest())
                .topupRequest(request.getTopupRequest())
                .walletRevenue(request.getWalletRevenue())
                .walletExpense(request.getWalletExpense())
                .beforeBalance(String.valueOf(request.getBalanceBefore()))
                .afterBalance(String.valueOf(request.getBalanceAfter()))
                .processedAt(request.getProcessedAt())
                .build();
        walletTransactionRepository.save(walletTransaction);
    }
}
