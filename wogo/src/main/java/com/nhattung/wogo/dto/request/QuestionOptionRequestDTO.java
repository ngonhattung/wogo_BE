package com.nhattung.wogo.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionOptionRequestDTO {

    private Long id;
    private String optionText;
    private boolean isCorrect;
    private boolean isActive;
    private int orderIndex;
    private Long questionId; // Assuming this is the ID of the related question
}
