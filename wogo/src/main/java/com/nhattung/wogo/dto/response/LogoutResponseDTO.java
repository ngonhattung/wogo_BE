package com.nhattung.wogo.dto.response;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class LogoutResponseDTO {
    private String message;
    private String status;
}
