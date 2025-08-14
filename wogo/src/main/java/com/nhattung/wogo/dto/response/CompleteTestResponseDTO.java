package com.nhattung.wogo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompleteTestResponseDTO {
    private WorkerVerificationResponseDTO workerVerification;
    private WorkerResponseDTO worker;

}
