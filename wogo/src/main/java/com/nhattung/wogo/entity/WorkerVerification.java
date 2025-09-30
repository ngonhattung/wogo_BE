package com.nhattung.wogo.entity;

import com.nhattung.wogo.enums.VerificationStatus;
import com.nhattung.wogo.enums.VerificationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class WorkerVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private VerificationType verificationType;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;

    private LocalDateTime approvedAt;

    private String rejectionReason;

    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "worker_verification_test_id")
    private WorkerVerificationTest workerVerificationTest;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "worker_document_id")
    private WorkerDocument workerDocument;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceWG service;


}
