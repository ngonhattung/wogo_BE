package com.nhattung.wogo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerResponseDTO {
    private Long id;
    private String description;
    private int totalJobs;
    private int totalReviews;
    private double averageRating;
    private UserResponseDTO user;
}
