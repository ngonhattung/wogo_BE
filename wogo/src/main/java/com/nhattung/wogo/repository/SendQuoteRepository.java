package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.WorkerQuote;
import com.nhattung.wogo.enums.JobRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SendQuoteRepository extends JpaRepository<WorkerQuote, Long> {
    @Query("""
        SELECT wq
        FROM WorkerQuote wq
        LEFT JOIN Booking b
          ON b.bookingCode = wq.job.jobRequestCode
        WHERE wq.worker.user.id = :currentUserId
          AND (
                :status = 'ALL'
                OR wq.job.status = :status
              )
          AND NOT (
                wq.job.status = 'ACCEPTED'
                AND b.bookingStatus IN (com.nhattung.wogo.enums.BookingStatus.CANCELLED,
                                        com.nhattung.wogo.enums.BookingStatus.COMPLETED)
              )
        ORDER BY wq.createdAt DESC
    """)
    List<WorkerQuote> findByWorkerUserIdAndStatus(Long currentUserId, JobRequestStatus status);

    @Query("""
    SELECT CASE WHEN COUNT(wq) > 0 THEN true ELSE false END
    FROM WorkerQuote wq
    WHERE wq.worker.user.id = :userId
      AND wq.job.service.id = :serviceId
      AND wq.job.status = 'PENDING'
      AND wq.job.bookingDate BETWEEN :startOfDay AND :endOfDay
""")
    boolean checkExistSendQuote(Long serviceId, Long userId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query("SELECT wq FROM WorkerQuote wq WHERE wq.job.jobRequestCode = :jobRequestCode")
    List<WorkerQuote> findByJobRequestCode(String jobRequestCode);
}
