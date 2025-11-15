package com.nhattung.wogo.repository;

import com.nhattung.wogo.dto.response.WithdrawalResponseDTO;
import com.nhattung.wogo.entity.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {

    @Query("""
        select new com.nhattung.wogo.dto.response.WithdrawalResponseDTO(
            w.amount,
            w.bankAccountNumber,
            w.bankName,
            w.requestedAt,
            wt.paymentStatus,
            wt.transactionType,
            wt.transactionCode,
            w.approved
        )
        from Withdrawal w
        join w.walletTransaction wt
        where (:isApproved is null or w.approved = :isApproved)
        order by w.requestedAt desc
    """)
    List<WithdrawalResponseDTO> findWithdrawalsByApprovalStatus(Boolean isApproved);
}
