package com.nhattung.wogo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ParentCategoryResponseDTO {
    private ServiceCategoryResponseDTO parent;
    private List<ServiceCategoryResponseDTO> children;
}
