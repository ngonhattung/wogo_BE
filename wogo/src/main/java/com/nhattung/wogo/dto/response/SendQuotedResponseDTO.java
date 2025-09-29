package com.nhattung.wogo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendQuotedResponseDTO {
    private Long id;
    private WorkerResponseDTO worker;
    private BigDecimal quotedPrice;
    private double distanceToJob;
}
