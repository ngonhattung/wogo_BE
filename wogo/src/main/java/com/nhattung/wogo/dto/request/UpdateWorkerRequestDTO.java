package com.nhattung.wogo.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateWorkerRequestDTO {
    private double rating;
}
