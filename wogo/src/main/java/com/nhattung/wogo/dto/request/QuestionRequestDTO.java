package com.nhattung.wogo.dto.request;

import com.nhattung.wogo.enums.DifficultyLevel;
import com.nhattung.wogo.enums.QuestionType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionRequestDTO {
    private String questionText;
    private QuestionType questionType;    //combobox
    private DifficultyLevel difficultyLevel; //combobox
    private String explanation;
    private String imageUrl;
    private Long questionCategoryId;
    private boolean isActive;
    private List<QuestionOptionRequestDTO> questionOptions;
}
