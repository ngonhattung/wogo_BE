package com.nhattung.wogo.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequestDTO {
    private String fullName;
    private String avatarUrl;
    private String password;
    private boolean isActive;
}
