package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingHistoryRepository extends JpaRepository<Job, Long> {
    @Query(value = """

            SELECT type, code, date, address, status, service_name
        FROM (
            -- Lịch sử JOB (chỉ có user_id, chỉ lấy CANCELLED)
            SELECT\s
                   'JOB' AS type,
                   j.job_request_code AS code,
                   j.booking_date AS date,
                   j.booking_address AS address,
                   j.status AS status,
                   s.service_name AS service_name,
                   j.user_id AS user_id,
                   NULL AS worker_id
            FROM job j
            JOIN servicewg s ON j.service_id = s.id
            WHERE j.status = 'CANCELLED'
        
            UNION ALL
        
            -- Lịch sử BOOKING (có user_id, worker_id, chỉ lấy COMPLETED)
            SELECT\s
                   'BOOKING' AS type,
                   b.booking_code AS code,
                   b.booking_date AS date,
                   b.booking_address AS address,
                   b.booking_status AS status,
                   s.service_name AS service_name,
                   b.user_id AS user_id,
                   b.worker_id AS worker_id
            FROM booking b
            JOIN servicewg s ON b.service_id = s.id
            LEFT JOIN worker w ON b.worker_id = w.id
            WHERE b.booking_status = 'COMPLETED'
        ) AS combined
        WHERE\s
            -- Nếu là khách
            (:isWorker = false AND user_id = :userId)
            -- Nếu là thợ → tìm tất cả booking có worker_id ứng với worker có user_id này
            OR (:isWorker = true AND (
                worker_id IN (SELECT id FROM worker WHERE user_id = :userId)
            ))
        ORDER BY date DESC;
        """, nativeQuery = true)
    List<Object[]> findBookingHistoryByUser(Long userId, boolean isWorker);
}
