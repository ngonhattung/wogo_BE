package com.nhattung.wogo.dto.response;

import lombok.Data;

@Data
public class QuestionCategoryResponseDTO {

    private Long id;
    private String questionCategoryName;
    private ServiceResponseDTO service;
    private double requiredScore;
    private String description;
    private int questionPerTest;
}
