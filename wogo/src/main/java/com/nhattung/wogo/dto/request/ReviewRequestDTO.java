package com.nhattung.wogo.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewRequestDTO {
    private Long bookingId;
    private int rating;
    private String comment;
}
