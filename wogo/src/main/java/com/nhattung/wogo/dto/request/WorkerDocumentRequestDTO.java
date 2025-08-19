package com.nhattung.wogo.dto.request;

import com.nhattung.wogo.entity.User;
import com.nhattung.wogo.enums.DocumentType;
import com.nhattung.wogo.enums.VerificationStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkerDocumentRequestDTO {
    private Long id;
    private DocumentType documentType;
    private String documentName;
    private VerificationStatus verificationStatus;
    private Long serviceId;
}
