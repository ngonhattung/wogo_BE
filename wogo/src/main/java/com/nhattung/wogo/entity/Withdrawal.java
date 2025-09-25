package com.nhattung.wogo.entity;

import com.nhattung.wogo.enums.Bank;
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
public class Withdrawal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bankAccountNumber;
    private BigDecimal amount;
    private BigDecimal taxAmount;
    private BigDecimal netAmount;
    private Boolean approved;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private PaymentMethod withdrawalMethod;

    @Enumerated(EnumType.STRING)
    private Bank bankName;

    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private String rejectionReason;

    @CreationTimestamp
    private Timestamp createdAt;

    @OneToOne
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @OneToOne(mappedBy = "withdrawal", cascade = CascadeType.ALL, orphanRemoval = true)
    private WalletTransaction walletTransaction;
}
