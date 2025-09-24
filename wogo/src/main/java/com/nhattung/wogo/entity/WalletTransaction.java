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
    @JoinColumn(name = "topup_request_id", nullable = false)
    private TopupRequest topupRequest;

    @OneToOne
    @JoinColumn(name = "withdrawal_request_id", nullable = false)
    private WithdrawalRequest withdrawalRequest;

    @ManyToOne
    @JoinColumn(name = "walletRevenue_id", nullable = false)
    private WorkerWalletRevenue walletRevenue;

    @ManyToOne
    @JoinColumn(name = "walletExpense_id", nullable = false)
    private WorkerWalletExpense walletExpense;

}
