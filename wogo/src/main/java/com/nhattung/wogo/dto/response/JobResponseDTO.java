package com.nhattung.wogo.dto.response;

import com.nhattung.wogo.enums.JobRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponseDTO {
    private Long id;
    private String jobRequestCode;
    private LocalDateTime bookingDate;
    private double distance;
    private String description;
    private String bookingAddress;
    private BigDecimal estimatedPriceLower;
    private BigDecimal estimatedPriceHigher;
    private int estimatedDurationMinutes;
    private JobRequestStatus status;
    private List<JobFileResponseDTO> files;
    private UserResponseDTO user;
    private List<SendQuotedResponseDTO> workerQuotes;
    private ServiceResponseDTO service;
    private Long acceptedBy;
}
