package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.Deposit;
import com.nhattung.wogo.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Long> {
    Optional<Deposit> findByWalletTransaction(WalletTransaction walletTransaction);
}
