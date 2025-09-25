package com.nhattung.wogo.dto.response;

import com.nhattung.wogo.enums.Bank;
import com.nhattung.wogo.enums.PaymentStatus;
import com.nhattung.wogo.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class WithdrawalResponseDTO {
    private BigDecimal amount;
    private String bankAccountNumber;
    private Bank bankName;
    private LocalDateTime requestedAt;
    private PaymentStatus paymentStatus;
    private TransactionType transactionType;
    private String transactionCode;
}
