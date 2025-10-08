package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.request.ChatMessageRequestDTO;
import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.ChatResponseDTO;
import com.nhattung.wogo.service.chat.message.IChatMessageService;
import com.nhattung.wogo.service.chat.room.IChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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


        ChatResponseDTO chatMessageSaved = chatMessageService.saveMessages(ChatMessageRequestDTO.builder()
                .roomCode(request.getRoomCode())
                .content(request.getContent())
                .senderType(request.getSenderType())
                .build());

        // Gửi lại cho tất cả subscriber trong topic
        messagingTemplate.convertAndSend("/topic/chat/" + chatMessageSaved.getChatRoom(), chatMessageSaved);

        return ApiResponse.<Void>builder()
                .message("Message sent successfully")
                .build();
    }

    @PostMapping("/send-file")
    public ApiResponse<Void> sendFile(@ModelAttribute ChatMessageRequestDTO request,
                                      @RequestParam(value = "files", required = false) List<MultipartFile> files){

        ChatResponseDTO chatMessageSaved = chatMessageService.saveFiles(ChatMessageRequestDTO.builder()
                .roomCode(request.getRoomCode())
                .content(request.getContent())
                .senderType(request.getSenderType())
                .build(),files);

        // Gửi lại cho tất cả subscriber trong topic
        messagingTemplate.convertAndSend("/topic/chat/" + chatMessageSaved.getChatRoom(), chatMessageSaved);

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
