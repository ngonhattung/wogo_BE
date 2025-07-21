package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Method to find a role by its name
    Optional<Role> findByRoleName(String roleName);

}
