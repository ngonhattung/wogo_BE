package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.WorkerDocumentFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerDocumentFileRepository extends JpaRepository<WorkerDocumentFile, Long> {
    Optional<List<WorkerDocumentFile>> findByWorkerDocumentId(Long id);
}
