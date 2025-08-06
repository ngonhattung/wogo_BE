package com.nhattung.wogo.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceCategoryRequestDTO {

    private String categoryName;
    private String description;
    private Long parentId;
    private boolean isActive;
    private String icon;
}
