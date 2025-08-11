package com.nhattung.wogo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionUpdateResponseDTO {
    private QuestionResponseDTO currentQuestion;
    private List<OptionResponseDTO> serviceCategoriesOptions;
    private List<OptionResponseDTO> questionTypeOptions;
    private List<OptionResponseDTO> difficultyLevelOptions;
}
