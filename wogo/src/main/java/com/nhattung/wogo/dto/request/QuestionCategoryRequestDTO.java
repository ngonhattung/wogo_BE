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

    private String categoryServiceName;
    private double requiredScore;
    private int totalQuestion;
    private int questionPerTest;
    private boolean isActive;
    private String description;
}
