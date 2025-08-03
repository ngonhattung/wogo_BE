package com.nhattung.wogo.entity;

import com.nhattung.wogo.enums.TestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class WorkerVerificationTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String testCode;
    private int totalQuestions;
    private int correctAnswers;
    private double scorePercentage;
    private double passThreshold;
    private boolean isPassed;

    @Enumerated(EnumType.STRING)
    private TestStatus testStatus;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private int timeLimitMinutes;

    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "question_category_id", nullable = false)
    private QuestionCategory questionCategory;

    @OneToMany(mappedBy = "workerVerificationTest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestAnswer> testAnswers;

    @OneToOne(mappedBy = "workerVerificationTest", cascade = CascadeType.ALL, orphanRemoval = true)
    private WorkerVerification workerVerification;
}
