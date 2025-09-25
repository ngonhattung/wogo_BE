package com.nhattung.wogo.service.chat;

import com.nhattung.wogo.dto.request.ChatMessageRequestDTO;
import com.nhattung.wogo.dto.response.ChatResponseDTO;
import com.nhattung.wogo.entity.ChatMessage;
import com.nhattung.wogo.entity.ChatRoom;
import com.nhattung.wogo.entity.User;
import com.nhattung.wogo.enums.MessageType;
import com.nhattung.wogo.repository.ChatMessageRepository;
import com.nhattung.wogo.service.user.IUserService;
import com.nhattung.wogo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService implements IChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final IUserService userService;
    private final IChatFileService chatFileService;
    private final ModelMapper modelMapper;

    @Override
    public ChatResponseDTO saveMessages(ChatMessageRequestDTO request) {
        User user = userService.getUserByIdEntity(SecurityUtils.getCurrentUserId());
        ChatMessage chatMessage = createChatMessage(request, user);
        return convertToResponseDTO(chatMessageRepository.save(chatMessage));
    }

    private ChatMessage createChatMessage(ChatMessageRequestDTO request, User user) {
        return ChatMessage.builder()
                .messageType(MessageType.TEXT)
                .content(request.getContent())
                .senderType(request.getSenderType())
                .sender(user)
                .isRead(false)
                .isDeleted(false)
                .replyToMessageId(null)
                .chatRoom(request.getChatRoom())
                .build();
    }

    @Override
    public ChatResponseDTO saveFiles(ChatMessageRequestDTO request, List<MultipartFile> files) {
        User user = userService.getUserByIdEntity(SecurityUtils.getCurrentUserId());
        ChatMessage chatMessage = createChatMessage(request, user);
        chatFileService.saveChatFile(chatMessage, files);

        return convertToResponseDTO(chatMessageRepository.save(chatMessage));
    }

    @Override
    public List<ChatResponseDTO> getMessages(ChatRoom chatRoom) {
        return chatMessageRepository.findByChatRoomOrderByCreatedAtAsc(chatRoom).stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    private ChatResponseDTO convertToResponseDTO(ChatMessage chatMessage) {
        return modelMapper.map(chatMessage, ChatResponseDTO.class);
    }
}
