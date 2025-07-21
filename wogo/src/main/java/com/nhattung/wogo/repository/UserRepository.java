package com.nhattung.wogo.repository;


import com.nhattung.wogo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByPhone(String phone);

    boolean existsByPhone(String phone);
}
