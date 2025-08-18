package com.nhattung.wogo.dto.response;

import com.nhattung.wogo.enums.TestStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkerVerificationTestResponseDTO {
    private Long id;
    private String testCode;
    private int totalQuestions;
    private int correctAnswers;
    private double scorePercentage;
    private double passThreshold;
    private boolean isPassed;
    private TestStatus testStatus;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private int timeLimitMinutes;
}
