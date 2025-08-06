package com.nhattung.wogo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceCategoryResponseDTO {

    private Long id;
    private String categoryName;
    private String description;
    private Long parentId;
    private String icon;
}
