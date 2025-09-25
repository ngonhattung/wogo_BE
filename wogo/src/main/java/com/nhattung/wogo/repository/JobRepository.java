package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.Job;
import com.nhattung.wogo.enums.JobRequestStatus;
import io.micrometer.common.KeyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("SELECT j FROM Job j WHERE j.jobRequestCode = :jobRequestCode AND j.bookingDate >= :now")
    Optional<Job> findValidJobByJobRequestCode(String jobRequestCode, LocalDateTime now);

    @Query("""
                SELECT j
                FROM Job j
                WHERE j.user.id = :userId
                  AND j.bookingDate >= :now
                  AND (:status = 'ALL' OR j.status = :status)
            """)
    Optional<List<Job>> findValidJobsByUserId(Long userId,JobRequestStatus status, LocalDateTime now);

    @Query("SELECT j FROM Job j WHERE j.service.id = :serviceId AND j.bookingDate >= :now AND j.status = :status")
    Optional<List<Job>> getValidJobsByServiceId(Long serviceId, JobRequestStatus status, LocalDateTime now);
}
