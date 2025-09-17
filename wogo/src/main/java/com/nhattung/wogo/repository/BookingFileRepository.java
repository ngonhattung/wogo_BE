package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.BookingFile;
import io.micrometer.common.KeyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingFileRepository extends JpaRepository<BookingFile, Long> {
    List<BookingFile> findByBookingId(Long bookingId);
}
