package com.nhattung.wogo.dto.request;

import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Builder
public class RealtimeLocationDTO {
    private double latitude;
    private double longitude;
    private Long updatedAt;
}
