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

    private String bookingDate;
    private double distance;
    private double latitudeUser;
    private double longitudeUser;
}
