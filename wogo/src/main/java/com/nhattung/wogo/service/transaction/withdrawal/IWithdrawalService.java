package com.nhattung.wogo.service.transaction.withdrawal;

import com.nhattung.wogo.dto.request.ProcessWithdrawalRequestDTO;
import com.nhattung.wogo.dto.request.WithdrawalRequestDTO;
import com.nhattung.wogo.dto.response.WithdrawalResponseDTO;

public interface IWithdrawalService {
    WithdrawalResponseDTO createWithdrawalRequest(WithdrawalRequestDTO request);
    void processWithdrawal(ProcessWithdrawalRequestDTO request);
}
