package com.nhattung.wogo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ParentServiceResponseDTO {
    private ServiceResponseDTO parentService;
    private List<ServiceResponseDTO> childServices;
}
