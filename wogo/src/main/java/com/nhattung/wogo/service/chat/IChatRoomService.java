package com.nhattung.wogo.service.chat;

import com.nhattung.wogo.dto.request.ChatRoomRequestDTO;
import com.nhattung.wogo.entity.ChatRoom;

public interface IChatRoomService {
    void saveChatRoom(ChatRoomRequestDTO request);
    ChatRoom getChatRoomByRoomCode(String roomCode);
    void updateChatRoom(ChatRoom chatRoom);
}
