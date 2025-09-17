package com.nhattung.wogo.dto.request;

import com.nhattung.wogo.enums.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentRequestDTO {
    private String bookingCode;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private LocalDateTime paidAt;
}
