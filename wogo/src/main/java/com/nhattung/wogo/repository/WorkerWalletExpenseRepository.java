package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.WorkerWalletExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerWalletExpenseRepository extends JpaRepository<WorkerWalletExpense, Long> {
    boolean existsByWorkerId(Long workerId);
}
