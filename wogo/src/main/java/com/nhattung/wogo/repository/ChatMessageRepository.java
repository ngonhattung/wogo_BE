package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.ChatMessage;
import com.nhattung.wogo.entity.ChatRoom;
import io.micrometer.common.KeyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Optional<ChatMessage> findByChatRoomOrderByCreatedAtAsc(ChatRoom chatRoom);
}
