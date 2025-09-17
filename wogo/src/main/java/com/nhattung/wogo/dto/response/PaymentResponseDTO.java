package com.nhattung.wogo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponseDTO {
    private Long id;
    private String paymentMethod;
    private String amount;
    private String serviceAmount;
    private String platformFee;
    private String workerReceiveAmount;
    private String notes;
    private String paidAt;
}
