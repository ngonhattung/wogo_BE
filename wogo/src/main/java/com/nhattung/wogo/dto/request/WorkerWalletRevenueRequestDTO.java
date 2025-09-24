package com.nhattung.wogo.dto.request;

import com.nhattung.wogo.entity.Worker;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WorkerWalletRevenueRequestDTO {
    private Worker worker;
    private BigDecimal totalRevenue;
    private BigDecimal revenueBalance;
    private boolean isActive;
}
