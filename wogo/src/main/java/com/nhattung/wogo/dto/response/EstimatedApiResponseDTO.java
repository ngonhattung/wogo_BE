package com.nhattung.wogo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class EstimatedApiResponseDTO {
    private int serviceId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private int recordsFound;
}
