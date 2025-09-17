package com.nhattung.wogo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingFileDTO {
    private Long id;
    private String fileName;
    private String fileType;
    private String fileUrl;
}
