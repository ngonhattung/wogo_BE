package com.nhattung.wogo.dto.request;

import com.nhattung.wogo.entity.*;
import com.nhattung.wogo.enums.PaymentStatus;
import com.nhattung.wogo.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class WalletTransactionRequestDTO {
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private TransactionType transactionType;
    private PaymentStatus status;
    private String description;
    private Withdrawal withdrawal;
    private Deposit deposit;
    private WorkerWalletRevenue walletRevenue;
    private WorkerWalletExpense walletExpense;
    private LocalDateTime processedAt;
}
