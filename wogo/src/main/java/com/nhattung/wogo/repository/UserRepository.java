package com.nhattung.wogo.repository;


import com.nhattung.wogo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByPhone(String phone);

    boolean existsByPhone(String phone);

    List<User> findUsersByPhoneContaining(String phone);

    @Query("""
        SELECT COUNT(u)
        FROM User u
        WHERE NOT EXISTS (
            SELECT 1
            FROM UserRole ur
            WHERE ur.user = u
              AND ur.role.roleName IN ('ROLE_WORKER', 'ROLE_ADMIN')
        )
    """)
    int countCustomer();

    @Query("""
        SELECT FUNCTION('MONTH', u.createdAt) AS month,
           COUNT(u) AS total
        FROM User u
        WHERE FUNCTION('YEAR', u.createdAt) = :year
        GROUP BY FUNCTION('MONTH', u.createdAt)
        ORDER BY month
    """)
    List<Object[]> countRegisteredUsersByYear(int year);
}
