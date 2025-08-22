package com.nhattung.wogo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PricingSuggestResponseDTO {
    private Double suggestedPrice;
    private PriceRange range;
    private ModelInfo model;
    
    @Data
    @Builder
    public static class PriceRange {
        private Double low;
        private Double high;
    }

    @Data
    @Builder
    public static class ModelInfo {
        private Integer version;
        private Double mape;
    }
}
