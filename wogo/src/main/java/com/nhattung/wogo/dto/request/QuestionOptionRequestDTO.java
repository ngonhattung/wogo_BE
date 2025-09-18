package com.nhattung.wogo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionOptionRequestDTO {


    @NotBlank(message = "OPTION_TEXT_NOT_BLANK_MESSAGE")
    private String optionText;

    private boolean isCorrect;

    @Min(value = 1, message = "ORDER_INDEX_MIN_MESSAGE")
    private int orderIndex;

    private Long questionId;
}
