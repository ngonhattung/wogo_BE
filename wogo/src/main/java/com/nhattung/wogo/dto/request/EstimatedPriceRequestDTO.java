package com.nhattung.wogo.dto.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class EstimatedPriceRequestDTO {
    private Long serviceId;
    private Double distanceKm;
}
