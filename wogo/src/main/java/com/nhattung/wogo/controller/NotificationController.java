package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.NotificationResponseDTO;
import com.nhattung.wogo.enums.ROLE;
import com.nhattung.wogo.service.notification.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final INotificationService notificationService;

    @GetMapping("/my-notifications/{role}")
    public ApiResponse<List<NotificationResponseDTO>> getMyNotifications(@PathVariable ROLE role) {
        return ApiResponse.<List<NotificationResponseDTO>>builder()
                .result(notificationService.getNotificationsForCurrentUser(role))
                .message("Get notifications successfully")
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<NotificationResponseDTO> getNotificationById(@PathVariable Long id) {
        return ApiResponse.<NotificationResponseDTO>builder()
                .result(notificationService.getNotificationById(id))
                .message("Get notification successfully")
                .build();
    }
}
