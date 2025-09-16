package com.nhattung.wogo.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HaversineRequestDTO {
    private Double latCustomer;
    private Double lonCustomer;
    private Double latWorker;
    private Double lonWorker;
}
