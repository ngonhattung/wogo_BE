package com.nhattung.wogo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class FindServiceRequestDTO {
    private Long serviceId;

    @NotBlank(message = "EMPTY_DESCRIPTION")
    private String description;

    @NotBlank(message = "EMPTY_ADDRESS")
    private String address;

    private LocalDateTime bookingDate;
    private double latitudeUser;
    private double longitudeUser;


    //Estimated
    private BigDecimal estimatedPriceLower;
    private BigDecimal estimatedPriceHigher;
    private int estimatedDurationMinutes;

}
