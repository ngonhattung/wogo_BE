package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.WorkerService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerServiceRepository extends JpaRepository<WorkerService, Long> {

    List<WorkerService> findAllByWorkerId(Long workerId);
    boolean existsByWorkerIdAndServiceId(Long workerId, Long serviceId);
}
