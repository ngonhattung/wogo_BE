package com.nhattung.wogo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class WorkerQuote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal quotedPrice;

    @ManyToOne
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
