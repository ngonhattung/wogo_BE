package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.WorkerVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkerVerificationRepository extends JpaRepository<WorkerVerification, Long> {
    Optional<WorkerVerification> findByWorkerVerificationTestId(Long workerTestId);
}
