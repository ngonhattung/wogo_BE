package com.nhattung.wogo.service.transaction.deposit;

import com.nhattung.wogo.dto.request.DepositRequestDTO;
import com.nhattung.wogo.dto.request.ProcessDepositRequestDTO;
import com.nhattung.wogo.entity.Deposit;

public interface IDepositService {
    Deposit createDepositRequest(DepositRequestDTO request);
    Deposit processDeposit(ProcessDepositRequestDTO request);
}
