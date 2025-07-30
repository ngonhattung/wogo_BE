package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist,Long> {
    boolean existsByTokenJti(String jti);

    void deleteByExpiryDateBefore(Date expiryDate);
}
