package com.nhattung.wogo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class JobFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String fileUrl;

    private String fileType;

    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
}