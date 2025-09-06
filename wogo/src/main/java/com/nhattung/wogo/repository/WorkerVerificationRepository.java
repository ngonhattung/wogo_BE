package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.WorkerVerification;
import com.nhattung.wogo.enums.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerVerificationRepository extends JpaRepository<WorkerVerification, Long> {
    Optional<WorkerVerification> findByWorkerVerificationTestId(Long workerTestId);

    Optional<WorkerVerification>findByWorkerDocumentId(Long id);

    List<WorkerVerification> findByServiceIdAndUserIdAndVerificationType(Long serviceId, Long userId, VerificationType type);
}
