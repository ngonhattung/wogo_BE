package com.nhattung.wogo.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NegotiatePriceRequestDTO {
    private String bookingCode;
    private BigDecimal finalPrice;
    private String notes;
}
