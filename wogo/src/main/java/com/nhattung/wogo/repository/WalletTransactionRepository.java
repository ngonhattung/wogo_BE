package com.nhattung.wogo.repository;

import com.nhattung.wogo.dto.response.WalletTransactionResponseDTO;
import com.nhattung.wogo.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    @Query("""
        select new com.nhattung.wogo.dto.response.WalletTransactionResponseDTO(
            wt.id,
            wt.transactionCode,
            wt.transactionType,
            wt.amount,
            wt.beforeBalance,
            wt.afterBalance,
            wt.paymentStatus,
            wt.description,
            wt.processedAt,
            wt.createdAt
        )
        from WalletTransaction wt
        join wt.withdrawal w
        where w.worker.id = :workerId
          and wt.paymentStatus <> 'PENDING'
        order by wt.createdAt desc
    """)
    List<WalletTransactionResponseDTO> getHistoryWithdrawalTransactions(Long workerId);

    @Query("""
        select new com.nhattung.wogo.dto.response.WalletTransactionResponseDTO(
            wt.id,
            wt.transactionCode,
            wt.transactionType,
            wt.amount,
            wt.beforeBalance,
            wt.afterBalance,
            wt.paymentStatus,
            wt.description,
            wt.processedAt,
            wt.createdAt
        )
        from WalletTransaction wt
        join wt.deposit d
         where d.worker.id = :workerId
          and wt.paymentStatus <> 'PENDING'
        order by wt.createdAt desc
    """)
    List<WalletTransactionResponseDTO> getHistoryDepositTransactions(Long workerId);
}
