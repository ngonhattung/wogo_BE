package com.nhattung.wogo.service.wallet.transaction;

import com.nhattung.wogo.dto.request.ProcessWalletTransactionRequestDTO;
import com.nhattung.wogo.dto.request.WalletTransactionRequestDTO;
import com.nhattung.wogo.dto.response.WalletTransactionResponseDTO;
import com.nhattung.wogo.entity.WalletTransaction;

import java.util.List;

public interface IWalletTransactionService {
    WalletTransaction saveWalletTransaction(WalletTransactionRequestDTO request);
    void processWalletTransaction(ProcessWalletTransactionRequestDTO request);
    List<WalletTransactionResponseDTO> getHistoryWithdrawalTransactions(Long workerId);
    List<WalletTransactionResponseDTO> getHistoryDepositTransactions(Long workerId);
}
