package com.nhattung.wogo.dto.request;

import com.nhattung.wogo.entity.QuestionCategory;
import com.nhattung.wogo.entity.User;
import com.nhattung.wogo.enums.TestStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WorkerVerificationTestRequestDTO {

    private boolean isPassed;
    private TestStatus testStatus;
    private int correctAnswers;
    private QuestionCategory questionCategory;
    private User user;
}
