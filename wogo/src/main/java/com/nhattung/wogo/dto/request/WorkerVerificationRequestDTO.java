package com.nhattung.wogo.dto.request;

import com.nhattung.wogo.entity.ServiceWG;
import com.nhattung.wogo.entity.WorkerDocument;
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
    private LocalDateTime approvedAt;
    private String rejectionReason;
    private WorkerVerificationTest verificationTest;
    private VerificationStatus verificationStatus;
    private ServiceWG service;
    private WorkerDocument workerDocument;
}
