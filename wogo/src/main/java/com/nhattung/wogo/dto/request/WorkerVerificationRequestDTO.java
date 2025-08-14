package com.nhattung.wogo.dto.request;

import com.nhattung.wogo.entity.User;
import com.nhattung.wogo.entity.WorkerVerificationTest;
import com.nhattung.wogo.enums.VerificationStatus;
import com.nhattung.wogo.enums.VerificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WorkerVerificationRequestDTO {
    private VerificationType verificationType;
    private boolean documentVerified;
    private LocalDateTime approvedAt;
    private String rejectionReason;
    private WorkerVerificationTest verificationTest;
    private User user;
    private VerificationStatus verificationStatus;
}
