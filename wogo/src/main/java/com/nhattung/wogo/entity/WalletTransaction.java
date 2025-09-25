package com.nhattung.wogo.entity;

import com.nhattung.wogo.enums.PaymentStatus;
import com.nhattung.wogo.enums.TransactionType;
import com.nhattung.wogo.enums.WalletType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

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



    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @OneToOne
    @JoinColumn(name = "deposit_id", nullable = false)
    private Deposit deposit;

    @OneToOne
    @JoinColumn(name = "withdrawal_id", nullable = false)
    private Withdrawal withdrawal;

    @ManyToOne
    @JoinColumn(name = "walletRevenue_id", nullable = false)
    private WorkerWalletRevenue walletRevenue;

    @ManyToOne
    @JoinColumn(name = "walletExpense_id", nullable = false)
    private WorkerWalletExpense walletExpense;

}
