package com.nhattung.wogo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponseDTO {
    private String linkTransaction;
    private Boolean transactionStatus;
}
