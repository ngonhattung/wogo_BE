package com.nhattung.wogo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class JwtResponseDTO {
    private String accessToken;
    private String refreshToken;
    private Date expirationDate;
}
