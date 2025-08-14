package com.nhattung.wogo.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AnswerRequestDTO {
    private Long questionId;
    private List<Long> selectedOptionIds;
}
