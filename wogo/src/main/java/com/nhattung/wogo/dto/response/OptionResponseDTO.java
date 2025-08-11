package com.nhattung.wogo.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OptionResponseDTO {
    private String value;
    private String label;
}
