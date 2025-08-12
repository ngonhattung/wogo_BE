package com.nhattung.wogo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class QuestionCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String questionCategoryName;
    private double requiredScore; // Điểm tối thiểu đạt
    private int questionsPerTest; // Số câu hỏi mỗi bài test
    private boolean isActive;
    private String description;

    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;

    @OneToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceWG service;

    @OneToMany(mappedBy = "questionCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;

    @OneToMany(mappedBy = "questionCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkerVerificationTest> workerVerificationTests;
}
