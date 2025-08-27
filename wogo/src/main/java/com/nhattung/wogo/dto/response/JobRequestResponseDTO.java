package com.nhattung.wogo.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String serviceName;
    private String description;
    private String bookingDate;
    private double distance;
    private BigDecimal estimatedPrice;
    private List<String> fileUrls;
    private UserResponseDTO user;

}
