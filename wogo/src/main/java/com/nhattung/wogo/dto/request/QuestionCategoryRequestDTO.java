package com.nhattung.wogo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionCategoryRequestDTO {
    private String questionCategoryName;
    private Long serviceId;
    private double requiredScore;
    private int questionPerTest;
    private boolean isActive;
    private String description;
}
