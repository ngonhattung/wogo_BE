package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.WorkerWalletRevenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerWalletRevenueRepository extends JpaRepository<WorkerWalletRevenue, Long> {
    boolean existsByWorkerId(Long workerId);
}
