package com.nhattung.wogo.dto.response;

import com.nhattung.wogo.enums.VerificationStatus;
import com.nhattung.wogo.enums.VerificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WorkerVerificationResponseDTO {
    private Long id;
    private VerificationType verificationType;
    private boolean documentVerified;
    private VerificationStatus verificationStatus;
    private LocalDateTime approvedAt;
    private String rejectionReason;
    private UserResponseDTO user;
    private WorkerVerificationTestResponseDTO workerVerificationTest;
    private ServiceResponseDTO service;
}
