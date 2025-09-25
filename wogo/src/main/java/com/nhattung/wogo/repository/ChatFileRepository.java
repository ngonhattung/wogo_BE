package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.ChatFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatFileRepository extends JpaRepository<ChatFile, Long> {
    Optional<List<ChatFile>> findByChatMessageId(Long messageId);
}
