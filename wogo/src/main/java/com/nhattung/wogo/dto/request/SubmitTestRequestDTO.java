package com.nhattung.wogo.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SubmitTestRequestDTO {
    private Long testId;
    private List<AnswerRequestDTO> answers;

    @Data
    @Builder
    public static class AnswerRequestDTO {
        private Long questionId;
        private List<Long> selectedOptionIds;
    }
}
