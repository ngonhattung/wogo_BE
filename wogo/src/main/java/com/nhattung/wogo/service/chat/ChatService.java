package com.nhattung.wogo.service.chat;


import com.nhattung.wogo.dto.request.ChatMessageRequestDTO;
import com.nhattung.wogo.dto.response.ChatResponseDTO;
import com.nhattung.wogo.dto.response.UploadS3Response;
import com.nhattung.wogo.utils.SecurityUtils;
import com.nhattung.wogo.utils.UploadToS3;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService implements IChatService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UploadToS3 uploadToS3;
    private static final String CHAT_KEY_PREFIX = "chat:";


    @Override
    public ChatResponseDTO saveMessages(ChatMessageRequestDTO request) {
        String key = CHAT_KEY_PREFIX + request.getJobRequestCode();
        Long senderId = SecurityUtils.getCurrentUserId();
        ChatResponseDTO chatMessage = ChatResponseDTO.builder()
                .jobRequestCode(request.getJobRequestCode())
                .senderId(senderId)
                .content(request.getContent())
                .timestamp(request.getTimestamp().toString())
                .build();

        redisTemplate.opsForList().rightPush(key, chatMessage);

        redisTemplate.expire(key, Duration.ofHours(4));

        return chatMessage;
    }

    @Override
    public ChatResponseDTO saveFiles(ChatMessageRequestDTO request, List<MultipartFile> files) {
        String key = CHAT_KEY_PREFIX + request.getJobRequestCode();
        Long senderId = SecurityUtils.getCurrentUserId();

        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            UploadS3Response uploaded = uploadToS3.uploadFileToS3(file);
            fileUrls.add(uploaded.getFileUrl());
        }

        ChatResponseDTO chatFile = ChatResponseDTO.builder()
                .jobRequestCode(request.getJobRequestCode())
                .senderId(senderId)
                .fileUrls(fileUrls) // nhóm tất cả ảnh vào đây
                .timestamp(request.getTimestamp().toString())
                .build();

        redisTemplate.opsForList().rightPush(key, chatFile);

        redisTemplate.expire(key, Duration.ofHours(4));

        return chatFile;
    }

    @Override
    public void deleteChat(String bookingCode) {
        String key = CHAT_KEY_PREFIX + bookingCode;
        redisTemplate.delete(key);
    }

    @Override
    public List<ChatResponseDTO> getMessages(String bookingCode) {
        String key = CHAT_KEY_PREFIX + bookingCode;
        List<Object> objects = redisTemplate.opsForList().range(key, 0, -1);

        if (objects == null || objects.isEmpty()) {
            return Collections.emptyList();
        }

        return objects.stream()
                .filter(ChatResponseDTO.class::isInstance)
                .map(ChatResponseDTO.class::cast)
                .toList();
    }

}
