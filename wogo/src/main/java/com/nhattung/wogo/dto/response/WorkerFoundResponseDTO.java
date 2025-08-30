package com.nhattung.wogo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WorkerFoundResponseDTO {

    private WorkerResponseDTO worker;
    private Double distance; // in kilometers
    private BigDecimal quotedPrice;
}
