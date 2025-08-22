package com.nhattung.wogo.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PricingSuggestRequestDTO {
    private long serviceId;
    private Integer durationMinutes;
    private Double distanceKm;
    private Integer hour;
    private Integer weekday;
}
