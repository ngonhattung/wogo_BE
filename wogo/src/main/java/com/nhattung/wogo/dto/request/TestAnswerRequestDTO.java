package com.nhattung.wogo.dto.request;

import com.nhattung.wogo.entity.WorkerVerificationTest;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestAnswerRequestDTO {
    private WorkerVerificationTest workerTest;
    private Long questionId;
    private Long questionOptionId;
    private boolean isCorrect;
}
