package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.WorkerQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SendQuoteRepository extends JpaRepository<WorkerQuote, Long> {
    Optional<WorkerQuote> findByWorkerUserId(Long currentUserId);

    @Query("""
    SELECT CASE WHEN COUNT(wq) > 0 THEN true ELSE false END
    FROM WorkerQuote wq
    WHERE wq.worker.user.id = :userId
      AND wq.job.service.id = :serviceId
      AND wq.job.status = 'PENDING'
      AND wq.job.bookingDate BETWEEN :startOfDay AND :endOfDay
""")
    boolean checkExistSendQuote(Long serviceId, Long userId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
