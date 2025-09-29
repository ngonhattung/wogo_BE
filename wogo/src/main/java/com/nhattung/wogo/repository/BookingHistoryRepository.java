package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingHistoryRepository extends JpaRepository<Job, Long> {
    @Query(value = """
            SELECT 'JOB' AS type,\s
                                 j.job_request_code AS code,\s
                                 j.booking_date AS date,\s
                                 j.booking_address AS address,
                                 j.status AS status,
                                 s.service_name  AS service_name
                          FROM job j
                          JOIN servicewg s ON j.service_id = s.id
                          WHERE j.status = 'CANCELLED'\s
                            AND j.user_id = :userId
                          
                          UNION ALL
                          
                          SELECT 'BOOKING' AS type,\s
                                 b.booking_code AS code,\s
                                 b.booking_date AS date,\s
                                 b.booking_address AS address,
                                 b.booking_status AS status,
                                 s.service_name  AS service_name
                          FROM booking b
                          JOIN servicewg s ON b.service_id = s.id
                          WHERE b.booking_status = 'COMPLETED'\s
                            AND b.user_id = :userId
                          
                          ORDER BY date DESC;
        """, nativeQuery = true)
    List<Object[]> findBookingHistoryByUser(Long userId);
}
