package com.nhattung.wogo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionCategoryUpdateResponseDTO {
    private QuestionCategoryResponseDTO currentQuestionCategory;
    private List<OptionResponseDTO> serviceCategoriesOptions;
}
