package com.nhattung.wogo.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcessWithdrawalRequestDTO {
    private Long withdrawalId;
    private boolean approved;
    private String rejectionReason;
}
