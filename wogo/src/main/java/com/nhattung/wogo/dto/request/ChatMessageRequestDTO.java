package com.nhattung.wogo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequestDTO {
    private String jobRequestCode;
    private Long workerId;
    private String content;
    private boolean isDelete;
    private LocalDateTime timestamp;
}
