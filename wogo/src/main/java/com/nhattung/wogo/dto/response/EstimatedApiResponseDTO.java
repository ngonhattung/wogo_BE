package com.nhattung.wogo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class EstimatedApiResponseDTO {
    private Range range;
    private int durationMinutes;

    @Data
    public static class Range {
        private BigDecimal low;
        private BigDecimal high;
    }
}
