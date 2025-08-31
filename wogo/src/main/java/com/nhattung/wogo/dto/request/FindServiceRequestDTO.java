package com.nhattung.wogo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class FindServiceRequestDTO {
    private Long serviceId;

    @NotBlank(message = "EMPTY_DESCRIPTION")
    private String description;

    @NotBlank(message = "EMPTY_ADDRESS")
    private String address;
    private BigDecimal estimatedPriceLower;
    private BigDecimal estimatedPriceHigher;
    private String bookingDate;
    private double distance;
}
