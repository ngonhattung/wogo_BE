package com.nhattung.wogo.service.chat.message;

import com.nhattung.wogo.dto.request.ChatMessageRequestDTO;
import com.nhattung.wogo.dto.response.ChatResponseDTO;
import com.nhattung.wogo.entity.ChatRoom;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IChatMessageService {
    ChatResponseDTO saveMessages(ChatMessageRequestDTO request);
    ChatResponseDTO saveFiles(ChatMessageRequestDTO request, List<MultipartFile> files);
    List<ChatResponseDTO> getMessages(ChatRoom chatRoom);
}
