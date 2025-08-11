package com.nhattung.wogo.dto.response;

import com.nhattung.wogo.enums.DifficultyLevel;
import com.nhattung.wogo.enums.QuestionType;
import lombok.Data;

import java.util.List;

@Data
public class QuestionResponseDTO {
    private Long id;
    private String questionText;
    private QuestionType questionType;
    private DifficultyLevel difficultyLevel;
    private String explanation;
    private String imageUrl;
    private QuestionCategoryResponseDTO questionCategory;
    private List<QuestionOptionResponseDTO> questionOptions;
}
