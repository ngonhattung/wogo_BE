package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.JobFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobFileRepository extends JpaRepository<JobFile, Long> {
    Optional<List<JobFile>> findByJobId(Long jobId);
}
