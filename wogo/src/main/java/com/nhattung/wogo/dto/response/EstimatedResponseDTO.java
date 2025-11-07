package com.nhattung.wogo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class EstimatedResponseDTO {
    private BigDecimal estimatedPriceLower;
    private BigDecimal estimatedPriceHigher;
}
