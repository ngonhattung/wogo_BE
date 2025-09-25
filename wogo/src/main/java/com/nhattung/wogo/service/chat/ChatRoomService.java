package com.nhattung.wogo.service.chat;


import com.nhattung.wogo.dto.request.ChatRoomRequestDTO;
import com.nhattung.wogo.entity.ChatRoom;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.ChatRoomRepository;
import com.nhattung.wogo.service.user.IUserService;
import com.nhattung.wogo.service.worker.IWorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService implements IChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    @Override
    public void saveChatRoom(ChatRoomRequestDTO request) {
        ChatRoom chatRoom = ChatRoom.builder()
                .roomCode(request.getJobRequestCode())
                .lastMessageAt(request.getLastMessageAt())
                .job(request.getJob())
                .isVisible(request.isVisible())
                .build();

        chatRoomRepository.save(chatRoom);
    }

    @Override
    public ChatRoom getChatRoomByRoomCode(String roomCode) {
        return chatRoomRepository.findByRoomCode(roomCode).orElseThrow(() -> new AppException(ErrorCode.CHAT_ROOM_NOT_FOUND));
    }

    @Override
    public void updateChatRoom(ChatRoom chatRoom) {
        chatRoomRepository.save(chatRoom);
    }

}
