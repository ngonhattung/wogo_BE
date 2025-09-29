package com.nhattung.wogo.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcessDepositRequestDTO {
    private Long depositId;
    private String rejectionReason;
}
