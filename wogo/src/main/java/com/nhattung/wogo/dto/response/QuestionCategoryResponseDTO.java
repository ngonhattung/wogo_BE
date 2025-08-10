package com.nhattung.wogo.dto.response;

import com.nhattung.wogo.entity.ServiceCategory;
import lombok.Builder;
import lombok.Data;

@Data
public class QuestionCategoryResponseDTO {

    private Long id;
    private ServiceCategory categoryService;
    private double requiredScore;
    private String description;
    private int totalQuestion;
    private int questionPerTest;
}
