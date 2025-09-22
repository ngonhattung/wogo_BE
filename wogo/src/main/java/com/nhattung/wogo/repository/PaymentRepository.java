package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.Booking;
import com.nhattung.wogo.entity.BookingPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<BookingPayment, Long> {
    Optional<BookingPayment> findByBooking(Booking booking);
}
