package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.request.ChatMessageRequestDTO;
import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.ChatResponseDTO;
import com.nhattung.wogo.entity.ChatRoom;
import com.nhattung.wogo.service.chat.message.IChatMessageService;
import com.nhattung.wogo.service.chat.room.IChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final IChatRoomService chatRoomService;
    private final IChatMessageService chatMessageService;

    @PostMapping("/send-message")
    public ApiResponse<Void> sendMessage(@RequestBody ChatMessageRequestDTO request) {

        ChatRoom chatRoom = chatRoomService.getChatRoomByRoomCode(request.getRoomCode());
        ChatResponseDTO chatMessageSaved = chatMessageService.saveMessages(ChatMessageRequestDTO.builder()
                .roomCode(request.getRoomCode())
                .content(request.getContent())
                .senderType(request.getSenderType())
                .chatRoom(chatRoom)
                .build());

        chatRoom.setLastMessageAt(LocalDateTime.now());
        chatRoomService.updateChatRoom(chatRoom);

        // Gửi lại cho tất cả subscriber trong topic
        messagingTemplate.convertAndSend("/topic/chat/" + request.getChatRoom(), chatMessageSaved);

        return ApiResponse.<Void>builder()
                .message("Message sent successfully")
                .build();
    }

    @PostMapping("/send-file")
    public ApiResponse<Void> sendFile(@ModelAttribute ChatMessageRequestDTO request,
                                      @RequestParam(value = "files", required = false) List<MultipartFile> files){

        ChatRoom chatRoom = chatRoomService.getChatRoomByRoomCode(request.getRoomCode());
        ChatResponseDTO chatMessageSaved = chatMessageService.saveFiles(ChatMessageRequestDTO.builder()
                .roomCode(request.getRoomCode())
                .content(request.getContent())
                .senderType(request.getSenderType())
                .chatRoom(chatRoom)
                .build(),files);

        chatRoom.setLastMessageAt(LocalDateTime.now());
        chatRoomService.updateChatRoom(chatRoom);

        // Gửi lại cho tất cả subscriber trong topic
        messagingTemplate.convertAndSend("/topic/chat/" + request.getChatRoom(), chatMessageSaved);

        return ApiResponse.<Void>builder()
                .message("Message sent successfully")
                .build();
    }


    @GetMapping("/get-messageBooking/{roomCode}")
    public ApiResponse<List<ChatResponseDTO>> getMessages(@PathVariable String roomCode)
    {
        return ApiResponse.<List<ChatResponseDTO>>builder()
                .message("Message sent successfully")
                .result(chatMessageService.getMessages(chatRoomService.getChatRoomByRoomCode(roomCode)))
                .build();
    }
}
