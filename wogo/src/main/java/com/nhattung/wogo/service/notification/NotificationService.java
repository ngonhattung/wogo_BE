package com.nhattung.wogo.service.notification;

import com.nhattung.wogo.dto.request.NotificationRequestDTO;
import com.nhattung.wogo.dto.response.NotificationResponseDTO;
import com.nhattung.wogo.entity.Notification;
import com.nhattung.wogo.entity.User;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.enums.ROLE;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.NotificationRepository;
import com.nhattung.wogo.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService{

    private final NotificationRepository notificationRepository;
    private final IUserService userService;
    private final ModelMapper modelMapper;

    @Override
    public void saveNotification(NotificationRequestDTO request) {

        User user = userService.getUserByIdEntity(request.getTargetUserId());

        Notification notification = Notification.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .imageUrl(request.getImageUrl())
                .targetRole(request.getTargetRole())
                .user(user)
                .build();
        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponseDTO> getNotificationsForCurrentUser(ROLE role) {
        return notificationRepository.findByTargetRoleOrderByCreatedAtDesc(role)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public NotificationResponseDTO getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
    }

    private NotificationResponseDTO convertToDTO(Notification notification) {
        return modelMapper.map(notification, NotificationResponseDTO.class);
    }

}
