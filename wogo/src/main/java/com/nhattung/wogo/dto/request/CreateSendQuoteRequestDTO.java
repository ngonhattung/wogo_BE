package com.nhattung.wogo.dto.request;

import com.nhattung.wogo.entity.Job;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CreateSendQuoteRequestDTO {
    private Job job;
    private BigDecimal quotedPrice;
    private double distanceToJob;
}
