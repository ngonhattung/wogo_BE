package com.nhattung.wogo.dto.request;

import com.nhattung.wogo.entity.Booking;
import com.nhattung.wogo.enums.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentRequestDTO {
    private Booking booking;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
}
