package com.nhattung.wogo.dto.response;

import lombok.Data;

@Data
public class WorkerDocumentFileResponseDTO {
    private Long id;
    private String fileName;
    private String fileType;
    private String fileUrl;
}
