package com.nhattung.wogo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class WorkerWalletExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal totalExpense;
    private BigDecimal expenseBalance;
    private boolean isActive;

    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;

    @OneToOne
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @OneToMany(mappedBy = "walletExpense", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WalletTransaction> walletTransactions;
}
