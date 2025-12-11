package com.nhattung.wogo.repository;

import com.nhattung.wogo.dto.response.WorkerWalletRevenueResponseDTO;
import com.nhattung.wogo.entity.WorkerWalletRevenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerWalletRevenueRepository extends JpaRepository<WorkerWalletRevenue, Long> {
    boolean existsByWorkerId(Long workerId);
    @Query("SELECT wwr FROM WorkerWalletRevenue wwr WHERE wwr.worker.user.id = :userId")
    WorkerWalletRevenue getWalletByUserId(Long userId);

    WorkerWalletRevenue getWorkerWalletRevenuesByWorkerId(Long workerId);
}
