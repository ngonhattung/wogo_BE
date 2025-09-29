package com.nhattung.wogo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateDepositResponseDTO {
    private String qrCodeUrl;
    private Long depositId;
}
