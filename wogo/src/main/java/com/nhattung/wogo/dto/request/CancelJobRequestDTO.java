package com.nhattung.wogo.dto.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CancelJobRequestDTO {
    private String jobRequestCode;
    private String reason;
}
