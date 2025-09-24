package com.nhattung.wogo.dto.request;

import com.nhattung.wogo.entity.Worker;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WorkerWalletExpenseRequestDTO {
    private Worker worker;
    private BigDecimal totalExpense;
    private BigDecimal expenseBalance;
    private boolean isActive;
}
