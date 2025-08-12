package com.nhattung.wogo.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionOptionRequestDTO {
    private String optionText;
    private boolean isCorrect;
    private int orderIndex;
    private Long questionId;
}
