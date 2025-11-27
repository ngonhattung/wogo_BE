package com.nhattung.wogo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRegistrationStatsResponseDTO {
    private int month;
    private long totalUsers;
}
