package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByUserIdAndRole(Long userId, String role);
}
