package com.nhattung.wogo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealtimeLocationDTO {
    private double latitude;
    private double longitude;
    private Long updatedAt;
}
