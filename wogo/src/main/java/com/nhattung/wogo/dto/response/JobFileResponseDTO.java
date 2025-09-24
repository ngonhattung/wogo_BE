package com.nhattung.wogo.dto.response;

import lombok.Data;

@Data
public class JobFileResponseDTO {
    private Long id;
    private String fileName;
    private String fileType;
    private String fileUrl;
}
