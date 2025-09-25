package com.nhattung.wogo.dto.request;

import com.nhattung.wogo.enums.Bank;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WithdrawalRequestDTO {
    private String bankAccountNumber;
    private Bank bankName;
    private BigDecimal amount;
}
