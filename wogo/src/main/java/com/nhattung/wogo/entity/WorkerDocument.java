package com.nhattung.wogo.entity;

import com.nhattung.wogo.enums.DocumentType;
import com.nhattung.wogo.enums.VerificationStatus;
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
public class WorkerDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;

    private String documentName;

    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;

    @OneToOne(mappedBy = "workerDocument", cascade = CascadeType.ALL, orphanRemoval = true)
    private WorkerVerification workerVerification;

    @OneToMany(mappedBy = "workerDocument", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkerDocumentFile> workerDocumentFiles;
}
