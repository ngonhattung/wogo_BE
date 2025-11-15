package com.nhattung.wogo.service.transaction.withdrawal;

import com.nhattung.wogo.dto.request.ProcessWithdrawalRequestDTO;
import com.nhattung.wogo.dto.request.WithdrawalRequestDTO;
import com.nhattung.wogo.dto.response.WithdrawalResponseDTO;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IWithdrawalService {
    WithdrawalResponseDTO createWithdrawalRequest(WithdrawalRequestDTO request);
    void processWithdrawal(ProcessWithdrawalRequestDTO request);
    List<WithdrawalResponseDTO> getWithdrawalsByApprovalStatus(Boolean isApproved);
}
