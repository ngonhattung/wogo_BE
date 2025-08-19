package com.nhattung.wogo.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionCategoryRequestDTO {

    @NotBlank(message = "EMPTY_QUESTION_CATEGORY_NAME")
    private String questionCategoryName;

    @NotNull(message = "SERVICE_ID_REQUIRED")
    private Long serviceId;

    @DecimalMin(value = "0.0", message = "REQUIRED_SCORE_RANGE_MESSAGE")
    @DecimalMax(value = "100.0", message = "REQUIRED_SCORE_RANGE_MESSAGE")
    private double requiredScore;

    @Min(value = 1, message = "QUESTION_PER_TEST_MIN_MESSAGE")
    private int questionPerTest;

    private boolean isActive;

    @Size(max = 255, message = "DESCRIPTION_MAX_MESSAGE")
    private String description;
}
