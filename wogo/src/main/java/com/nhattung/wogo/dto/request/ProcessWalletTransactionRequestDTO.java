package com.nhattung.wogo.dto.request;

import com.nhattung.wogo.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProcessWalletTransactionRequestDTO {
    private Long transactionId;
    private LocalDateTime processedAt;
    private PaymentStatus status;
}
