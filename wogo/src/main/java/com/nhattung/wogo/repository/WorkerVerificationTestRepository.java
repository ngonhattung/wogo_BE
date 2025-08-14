package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.WorkerVerificationTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerVerificationTestRepository extends JpaRepository<WorkerVerificationTest, Long> {
}
