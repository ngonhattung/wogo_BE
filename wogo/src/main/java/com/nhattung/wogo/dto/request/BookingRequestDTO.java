package com.nhattung.wogo.dto.request;

import com.nhattung.wogo.entity.ServiceWG;
import com.nhattung.wogo.entity.User;
import com.nhattung.wogo.enums.BookingStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequestDTO {
    private String bookingCode;
    private String title;
    private String description;
    private LocalDateTime bookingDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String bookingAddress;
    private BookingStatus bookingStatus;
    private int durationMinutes;
    private double distanceKm;
    private User user;
    private Long workerId;
    private ServiceWG service;
}
