package com.nhattung.wogo.dto.response;

import com.nhattung.wogo.enums.BookingStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingResponseDTO {
    private Long id;
    private String bookingCode;
    private UserResponseDTO user;
    private BookingStatus bookingStatus;
    private LocalDateTime bookingDate;
    private double distanceKm;
    private BigDecimal totalAmount;
    private ServiceResponseDTO service;
    private WorkerResponseDTO worker;
    private List<BookingFileDTO> files;
    private ReviewResponseDTO review;
}
