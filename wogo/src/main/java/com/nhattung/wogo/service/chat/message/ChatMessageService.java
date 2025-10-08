package com.nhattung.wogo.service.chat.message;

import com.nhattung.wogo.dto.request.ChatMessageRequestDTO;
import com.nhattung.wogo.dto.response.ChatResponseDTO;
import com.nhattung.wogo.entity.ChatMessage;
import com.nhattung.wogo.entity.ChatRoom;
import com.nhattung.wogo.entity.User;
import com.nhattung.wogo.enums.MessageType;
import com.nhattung.wogo.repository.ChatMessageRepository;
import com.nhattung.wogo.service.chat.file.IChatFileService;
import com.nhattung.wogo.service.chat.room.IChatRoomService;
import com.nhattung.wogo.service.user.IUserService;
import com.nhattung.wogo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService implements IChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final IUserService userService;
    private final IChatFileService chatFileService;
    private final ModelMapper modelMapper;
    private final IChatRoomService chatRoomService;

    @Override
    @Transactional
    public ChatResponseDTO saveMessages(ChatMessageRequestDTO request) {
        return saveChatMessage(request, null);
    }

    @Override
    @Transactional
    public ChatResponseDTO saveFiles(ChatMessageRequestDTO request, List<MultipartFile> files) {
        return saveChatMessage(request, files);
    }

    /**
     * Phương thức chung để lưu ChatMessage và cập nhật ChatRoom.
     * Nếu files != null, sẽ lưu file trước khi lưu message.
     */
    private ChatResponseDTO saveChatMessage(ChatMessageRequestDTO request, List<MultipartFile> files) {
        // Lấy chatRoom
        ChatRoom chatRoom = chatRoomService.getChatRoomByRoomCode(request.getRoomCode());

        // Tạo ChatMessage
        ChatMessage chatMessage = createChatMessage(request, chatRoom);

        // Nếu có files, lưu file
        if (files != null && !files.isEmpty()) {
            chatFileService.saveChatFile(chatMessage, files);
        }

        // Lưu ChatMessage
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        // Cập nhật thời gian lastMessage
        chatRoom.setLastMessageAt(LocalDateTime.now());
        chatRoomService.updateChatRoom(chatRoom);

        // Chuyển đổi sang DTO và trả về
        return convertToResponseDTO(savedMessage);
    }

    private ChatMessage createChatMessage(ChatMessageRequestDTO request, ChatRoom chatRoom) {
        return ChatMessage.builder()
                .messageType(MessageType.TEXT)
                .content(request.getContent())
                .senderType(request.getSenderType())
                .sender(userService.getCurrentUser())
                .isRead(false)
                .isDeleted(false)
                .replyToMessageId(null)
                .chatRoom(chatRoom)
                .build();
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
