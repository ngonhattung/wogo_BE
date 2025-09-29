package com.nhattung.wogo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingHistoryResponseDTO {
    private String type;        // JOB hoặc BOOKING
    private String code;        // job_request_code / booking_code
    private LocalDateTime date;
    private String address;
    private String status;      // CANCELLED / COMPLETED
    private String serviceName; // lấy từ ServiceWG
}
