package com.nhattung.wogo.dto.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CreateJobRequestDTO {
    private Long serviceId;
    private String description;
    private String address;
    private LocalDateTime bookingDate;
    private double distance;
    private BigDecimal estimatedPriceLower;
    private BigDecimal estimatedPriceHigher;
    private int estimatedDurationMinutes;
}
