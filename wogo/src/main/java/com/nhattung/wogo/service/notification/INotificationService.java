package com.nhattung.wogo.service.notification;

import com.nhattung.wogo.dto.request.NotificationRequestDTO;
import com.nhattung.wogo.dto.response.NotificationResponseDTO;
import com.nhattung.wogo.enums.ROLE;

import java.util.List;

public interface INotificationService {

    void saveNotification(NotificationRequestDTO request);
    List<NotificationResponseDTO> getNotificationsForCurrentUser(ROLE role);
    NotificationResponseDTO getNotificationById(Long id);
}
