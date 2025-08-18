package com.nhattung.wogo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompleteTestResponseDTO {
    private boolean isPassed;
    private double scorePercentage;

}
