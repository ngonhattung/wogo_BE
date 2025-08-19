package com.nhattung.wogo.dto.request;

import com.nhattung.wogo.enums.DifficultyLevel;
import com.nhattung.wogo.enums.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionRequestDTO {

    @NotBlank(message = "QUESTION_TEXT_NOT_BLANK_MESSAGE")
    private String questionText;

    @NotNull(message = "QUESTION_TYPE_NOT_NULL_MESSAGE")
    private QuestionType questionType;    //combobox

    @NotNull(message = "DIFFICULTY_LEVEL_NOT_NULL_MESSAGE")
    private DifficultyLevel difficultyLevel; //combobox

    private String explanation;

    @NotNull(message = "QUESTION_CATEGORY_ID_NOT_NULL_MESSAGE")
    private Long questionCategoryId;

    private Boolean isActive;

    @NotEmpty(message = "QUESTION_OPTIONS_NOT_EMPTY_MESSAGE")
    @Size(min = 1, message = "QUESTION_OPTIONS_SIZE_MESSAGE")
    @Valid   // rất quan trọng để validation chạy tiếp trong QuestionOptionRequestDTO
    private List<QuestionOptionRequestDTO> questionOptions;
}
