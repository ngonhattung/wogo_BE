package com.nhattung.wogo.service.chat;

import com.nhattung.wogo.dto.request.ChatMessageRequestDTO;
import com.nhattung.wogo.dto.response.ChatResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IChatService {

    ChatResponseDTO saveMessages(ChatMessageRequestDTO request);
    ChatResponseDTO saveFiles(ChatMessageRequestDTO request, List<MultipartFile> files);
    void deleteChat(String bookingCode);
    List<ChatResponseDTO> getMessages(String bookingCode);
}
