package com.nhattung.wogo.service.transaction;

import com.nhattung.wogo.dto.request.ProcessWithdrawalRequestDTO;
import com.nhattung.wogo.dto.request.WithdrawalRequestDTO;
import com.nhattung.wogo.dto.response.WithdrawalResponseDTO;
import com.nhattung.wogo.entity.Withdrawal;

public interface IWithdrawalService {
    WithdrawalResponseDTO createWithdrawalRequest(WithdrawalRequestDTO request);
    void processWithdrawal(ProcessWithdrawalRequestDTO request);
}
