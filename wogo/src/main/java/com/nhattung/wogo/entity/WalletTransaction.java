package com.nhattung.wogo.entity;

import com.nhattung.wogo.enums.PaymentStatus;
import com.nhattung.wogo.enums.TransactionType;
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
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionCode;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private BigDecimal amount;
    private String beforeBalance;
    private String afterBalance;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String description;
    private LocalDateTime processedAt;

    @CreationTimestamp
    private Timestamp createdAt;



    @OneToOne(mappedBy = "walletTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    @OneToOne
    @JoinColumn(name = "deposit_id")
    private Deposit deposit;

    @OneToOne
    @JoinColumn(name = "withdrawal_id")
    private Withdrawal withdrawal;

    @ManyToOne
    @JoinColumn(name = "walletRevenue_id")
    private WorkerWalletRevenue walletRevenue;

    @ManyToOne
    @JoinColumn(name = "walletExpense_id")
    private WorkerWalletExpense walletExpense;

}
