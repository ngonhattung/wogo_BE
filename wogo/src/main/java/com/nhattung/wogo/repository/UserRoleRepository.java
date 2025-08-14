package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.Role;
import com.nhattung.wogo.entity.User;
import com.nhattung.wogo.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    boolean existsByUserAndRole(User user, Role role);
}
