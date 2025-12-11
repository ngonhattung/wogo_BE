package com.nhattung.wogo.dto.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UpdateWalletRequestDTO {
    private BigDecimal amount;
    private boolean isAdd; // true: add, false: subtract
    private Long workerId;
}
