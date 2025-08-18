package com.nhattung.wogo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadS3Response {
    private String fileName;
    private String fileType;
    private String fileUrl;
}
