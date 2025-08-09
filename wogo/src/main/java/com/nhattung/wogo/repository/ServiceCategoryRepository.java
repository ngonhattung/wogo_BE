package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.ServiceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {
    Optional<ServiceCategory> findByCategoryName(String categoryServiceName);
    boolean existsByCategoryName(String categoryName);


    @Query("""
    SELECT c FROM ServiceCategory c
    LEFT JOIN ServiceCategory p ON c.parentId = p.id
    WHERE LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :name, '%'))
       OR LOWER(p.categoryName) LIKE LOWER(CONCAT('%', :name, '%'))
""")
    Page<ServiceCategory> searchByCategoryOrParentName
            (String name, Pageable pageable);
}
