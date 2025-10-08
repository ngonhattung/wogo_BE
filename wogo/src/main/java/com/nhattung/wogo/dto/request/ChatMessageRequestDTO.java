package com.nhattung.wogo.dto.request;

import com.nhattung.wogo.entity.ChatRoom;
import com.nhattung.wogo.enums.SenderType;
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
    private String roomCode;
    private String content;
    private SenderType senderType; // USER hoặc WORKER
}
