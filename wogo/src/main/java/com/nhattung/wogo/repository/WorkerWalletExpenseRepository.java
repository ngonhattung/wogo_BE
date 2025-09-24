package com.nhattung.wogo.repository;

import com.nhattung.wogo.dto.response.WorkerWalletExpenseResponseDTO;
import com.nhattung.wogo.entity.WorkerWalletExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerWalletExpenseRepository extends JpaRepository<WorkerWalletExpense, Long> {
    boolean existsByWorkerId(Long workerId);

    @Query("SELECT wwe FROM WorkerWalletExpense wwe WHERE wwe.worker.user.id = :currentUserId")
    WorkerWalletExpense getWalletByUserId(Long currentUserId);
}
