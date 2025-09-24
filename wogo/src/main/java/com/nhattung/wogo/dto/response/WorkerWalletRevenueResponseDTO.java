package com.nhattung.wogo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WorkerWalletRevenueResponseDTO {

    private Long id;
    private BigDecimal totalRevenue;
    private BigDecimal revenueBalance;
    private boolean isActive;
    private WorkerResponseDTO worker;
}
