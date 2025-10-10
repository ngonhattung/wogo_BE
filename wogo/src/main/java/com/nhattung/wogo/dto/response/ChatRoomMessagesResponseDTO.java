package com.nhattung.wogo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomMessagesResponseDTO {
    private ChatRoomResponseDTO chatRoom;
    private List<ChatResponseDTO> messages;
}
