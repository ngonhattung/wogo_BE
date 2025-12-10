package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.Booking;
import com.nhattung.wogo.entity.Payment;
import com.nhattung.wogo.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBooking(Booking booking);

    Optional<Payment> findByWalletTransaction(WalletTransaction walletTransaction);

    boolean existsByBooking(Booking booking);

    boolean existsByWalletTransaction(WalletTransaction walletTransaction);
}
