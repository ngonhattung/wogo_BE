package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.WorkerService;
import io.micrometer.common.KeyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerServiceRepository extends JpaRepository<WorkerService, Long> {

    @Query("SELECT CASE WHEN COUNT(ws) > 0 THEN true ELSE false END FROM WorkerService ws WHERE ws.worker.user.id = :userId AND ws.service.id = :serviceId")
    boolean existsByUserIdAndServiceId(Long userId, Long serviceId);
    @Query("SELECT ws FROM WorkerService ws WHERE ws.worker.user.id = :userId")
    List<WorkerService> findByUserId(Long userId);
}
