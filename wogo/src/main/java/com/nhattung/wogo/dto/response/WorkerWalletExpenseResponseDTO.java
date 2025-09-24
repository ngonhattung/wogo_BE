package com.nhattung.wogo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WorkerWalletExpenseResponseDTO {
    private Long id;
    private BigDecimal totalExpense;
    private BigDecimal expenseBalance;
    private boolean isActive;
    private WorkerResponseDTO worker;
}
