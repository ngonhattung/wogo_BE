package com.nhattung.wogo.dto.response;

import com.nhattung.wogo.enums.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponseDTO {
    private Long id;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private BigDecimal serviceAmount;
    private BigDecimal platformFee;
    private BigDecimal workerReceiveAmount;
    private String notes;
    private LocalDateTime paidAt;
}
