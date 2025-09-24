package com.nhattung.wogo.service.wallettransaction;

import com.nhattung.wogo.dto.request.WalletTransactionRequestDTO;

public interface IWalletTransaction {
    void saveWalletTransaction(WalletTransactionRequestDTO request);
}
