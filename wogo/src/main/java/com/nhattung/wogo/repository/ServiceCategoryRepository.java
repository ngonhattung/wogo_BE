package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.ServiceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {
    Optional<ServiceCategory> findByCategoryName(String categoryServiceName);
    boolean existsByCategoryName(String categoryName);
    Page<ServiceCategory> findByCategoryNameContainingIgnoreCaseAndActiveTrueAndParentIdNull(String name, Pageable pageable);
}
