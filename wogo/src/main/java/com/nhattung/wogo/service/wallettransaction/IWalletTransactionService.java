package com.nhattung.wogo.service.wallettransaction;

import com.nhattung.wogo.dto.request.WalletTransactionRequestDTO;

public interface IWalletTransactionService {
    void saveWalletTransaction(WalletTransactionRequestDTO request);
}
