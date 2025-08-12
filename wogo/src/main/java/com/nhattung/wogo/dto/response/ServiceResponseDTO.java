package com.nhattung.wogo.dto.response;

import lombok.Data;

@Data
public class ServiceResponseDTO {
    private Long id;
    private String serviceName;
    private String description;
    private String iconUrl;
    private Long parentId;
    private boolean isActive;
}
