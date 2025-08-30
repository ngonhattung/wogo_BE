package com.nhattung.wogo.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WorkerServiceResponseDTO {

    private Long id;
    private int totalOrders;
    private BigDecimal totalRevenue;
    private boolean isActive;
    private WorkerResponseDTO worker;
    private ServiceResponseDTO service;

}
