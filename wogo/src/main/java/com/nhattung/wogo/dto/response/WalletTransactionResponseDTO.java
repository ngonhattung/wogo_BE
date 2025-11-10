package com.nhattung.wogo.dto.response;

import com.nhattung.wogo.enums.PaymentStatus;
import com.nhattung.wogo.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionResponseDTO {
    private Long id;
    private String transactionCode;
    private TransactionType transactionType;
    private BigDecimal amount;
    private String beforeBalance;
    private String afterBalance;
    private PaymentStatus paymentStatus;
    private String description;
    private LocalDateTime processedAt;
    private Timestamp createdAt;
}
