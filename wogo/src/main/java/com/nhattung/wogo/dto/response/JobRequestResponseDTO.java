package com.nhattung.wogo.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nhattung.wogo.enums.JobRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRequestResponseDTO {
    private String jobRequestCode;
    private Long serviceId;
    private String serviceName;
    private String description;
    private String bookingDate;
    private double distance;
    private String bookingAddress;
    private BigDecimal estimatedPriceLower;
    private BigDecimal estimatedPriceHigher;
    private List<String> fileUrls;
    private UserResponseDTO user;
    private JobRequestStatus status;
    private Long acceptedBy;
}
