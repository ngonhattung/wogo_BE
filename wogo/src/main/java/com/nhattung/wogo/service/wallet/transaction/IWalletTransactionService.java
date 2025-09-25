package com.nhattung.wogo.service.wallet.transaction;

import com.nhattung.wogo.dto.request.ProcessWalletTransactionRequestDTO;
import com.nhattung.wogo.dto.request.WalletTransactionRequestDTO;
import com.nhattung.wogo.entity.WalletTransaction;

public interface IWalletTransactionService {
    WalletTransaction saveWalletTransaction(WalletTransactionRequestDTO request);
    void processWalletTransaction(ProcessWalletTransactionRequestDTO request);
}
