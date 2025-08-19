package com.nhattung.wogo.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SubmitTestRequestDTO {

    @NotNull(message = "TEST_ID_NOT_NULL_MESSAGE")
    private Long testId;

    @NotEmpty(message = "ANSWERS_NOT_EMPTY_MESSAGE")
    @Valid
    private List<AnswerRequestDTO> answers;

    @Data
    @Builder
    public static class AnswerRequestDTO {

        @NotNull(message = "QUESTION_ID_NOT_NULL_MESSAGE")
        private Long questionId;

        @NotEmpty(message = "SELECTED_OPTIONS_NOT_EMPTY_MESSAGE")
        private List<Long> selectedOptionIds;
    }
}
