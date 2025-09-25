package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.request.ChatMessageRequestDTO;
import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.ChatResponseDTO;
import com.nhattung.wogo.service.chat.IChatService;
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

    private final IChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/send-message")
    public ApiResponse<Void> sendMessage(@RequestBody ChatMessageRequestDTO request) {

        request.setTimestamp(LocalDateTime.now());
        ChatResponseDTO responseDTO = chatService.saveMessages(request);

        // Gửi lại cho tất cả subscriber trong topic
        messagingTemplate.convertAndSend("/topic/chat/" + request.getJobRequestCode() + "/worker/" + request.getWorkerId(), responseDTO);

        return ApiResponse.<Void>builder()
                .message("Message sent successfully")
                .build();
    }

    @PostMapping("/send-file")
    public ApiResponse<Void> sendFile(@ModelAttribute ChatMessageRequestDTO request,
                                      @RequestParam(value = "files", required = false) List<MultipartFile> files){

        request.setTimestamp(LocalDateTime.now());
        ChatResponseDTO responseDTO = chatService.saveFiles(request,files);

        // Gửi lại cho tất cả subscriber trong topic
        messagingTemplate.convertAndSend("/topic/chat/" + request.getJobRequestCode() + "/worker/" + request.getWorkerId(), responseDTO);

        return ApiResponse.<Void>builder()
                .message("Message sent successfully")
                .build();
    }


    @GetMapping("/get-messageBooking/{bookingCode}")
    public ApiResponse<List<ChatResponseDTO>> getMessages(@PathVariable String bookingCode)
    {
        return ApiResponse.<List<ChatResponseDTO>>builder()
                .message("Message sent successfully")
                .result(chatService.getMessages(bookingCode))
                .build();
    }
}
