package com.nhattung.wogo.entity;

import com.nhattung.wogo.enums.PaymentMethod;
import com.nhattung.wogo.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class TopupRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;
    private String transactionId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private PaymentMethod topupMethod;

    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private String rejectionReason;

    @CreationTimestamp
    private Timestamp createdAt;

    @OneToOne
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

//    @OneToOne
//    @JoinColumn(name = "payment_method_id", nullable = false)
//    private PaymentMethod paymentMethod;

    @OneToOne(mappedBy = "topupRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private WalletTransaction walletTransaction;
}
