package com.nhattung.wogo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponseDTO {
    private String linkTransaction;
    private LocalDateTime minDateTransaction;
    private LocalDateTime maxDateTransaction;
    private Boolean transactionStatus;
}
