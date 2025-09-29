package com.nhattung.wogo.service.chat.file;

import com.nhattung.wogo.entity.ChatFile;
import com.nhattung.wogo.entity.ChatMessage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IChatFileService {
    void saveChatFile(ChatMessage chatMessage, List<MultipartFile> files);
    List<ChatFile> getChatFilesByMessageId(Long messageId);
}
