package com.nhattung.wogo.dto.response;

import com.nhattung.wogo.enums.DocumentType;
import com.nhattung.wogo.enums.VerificationStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class WorkerDocumentResponseDTO {
    private Long id;
    private String documentName;
    private DocumentType documentType;
    private VerificationStatus verificationStatus;
    private UserResponseDTO user;
    private List<WorkerDocumentFileResponseDTO> documentFiles;
}
