package com.nhattung.wogo.dto.response;

import com.nhattung.wogo.enums.NotificationType;
import com.nhattung.wogo.enums.ROLE;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {
    private String title;
    private String description;
    private NotificationType type;
    private String imageUrl;
    private ROLE targetRole;
    private UserResponseDTO user;
}
