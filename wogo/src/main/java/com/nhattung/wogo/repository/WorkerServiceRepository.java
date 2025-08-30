package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.WorkerService;
import io.micrometer.common.KeyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerServiceRepository extends JpaRepository<WorkerService, Long> {

    boolean existsByWorkerIdAndServiceId(Long workerId, Long serviceId);
    @Query("SELECT ws FROM WorkerService ws WHERE ws.worker.user.id = :userId")
    List<WorkerService> findByUserId(Long userId);
}
