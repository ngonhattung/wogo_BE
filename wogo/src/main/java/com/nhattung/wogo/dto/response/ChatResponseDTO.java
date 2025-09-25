package com.nhattung.wogo.dto.response;

import com.nhattung.wogo.enums.MessageType;
import com.nhattung.wogo.enums.SenderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponseDTO {
    private String content;
    private MessageType messageType;
    private SenderType senderType;
    private List<ChatFileResponseDTO> fileUrls;
    private UserResponseDTO sender;
    private ChatRoomResponseDTO chatRoom;
}
