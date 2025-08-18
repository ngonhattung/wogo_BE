package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.ServiceWG;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceWG, Long> {
    boolean existsByServiceName(String serviceName);


    @Query("""
    SELECT DISTINCT c
    FROM ServiceWG c
    LEFT JOIN ServiceWG p ON c.parentId = p.id
    LEFT JOIN ServiceWG ch ON ch.parentId = c.id
    WHERE LOWER(c.serviceName) LIKE LOWER(CONCAT('%', :serviceName, '%'))
    OR LOWER(p.serviceName) LIKE LOWER(CONCAT('%', :serviceName, '%'))
    OR LOWER(ch.serviceName) LIKE LOWER(CONCAT('%', :serviceName, '%'))
""")
    Page<ServiceWG> searchByServiceOrParentName(String serviceName, Pageable pageable);

    List<ServiceWG> findAllByIsActiveTrueAndParentIdIsNull();
}
