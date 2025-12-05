package com.nhattung.wogo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerServiceResponseDTO {

    private Long id;
    private int totalOrders;
    private BigDecimal totalRevenue;
    private boolean isActive;
    private WorkerResponseDTO worker;
    private ParentServiceResponseDTO service;

}
