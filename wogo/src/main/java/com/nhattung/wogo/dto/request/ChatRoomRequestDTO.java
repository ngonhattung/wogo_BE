package com.nhattung.wogo.dto.request;

import com.nhattung.wogo.entity.Job;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatRoomRequestDTO {
    private String jobRequestCode;
    private LocalDateTime lastMessageAt;
    private Job job;
    private boolean isVisible;
}
