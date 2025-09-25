package com.nhattung.wogo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatRoomResponseDTO {
    private String roomCode;
    private boolean isVisible;
    private LocalDateTime lastMessageAt;
    private JobResponseDTO job;
}
