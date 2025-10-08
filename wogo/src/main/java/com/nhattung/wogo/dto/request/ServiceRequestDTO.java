package com.nhattung.wogo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceRequestDTO {

    @NotBlank(message = "EMPTY_SERVICE_NAME")
    private String serviceName;

    private String description;
    private Boolean isActive;
    private Long parentId;
}

