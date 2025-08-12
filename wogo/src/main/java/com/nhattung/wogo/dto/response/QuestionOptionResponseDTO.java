package com.nhattung.wogo.dto.response;

import com.nhattung.wogo.entity.Question;
import lombok.Data;

@Data
public class QuestionOptionResponseDTO {
    private Long id;
    private String optionText;
    private boolean isCorrect;
    private int orderIndex;
    private Long questionId;
}
