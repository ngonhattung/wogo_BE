package com.nhattung.wogo.dto.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PlaceJobRequestDTO {
    private String jobRequestCode;
    private Long workerId;
    private BigDecimal quotedPrice;
}
