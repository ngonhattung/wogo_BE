package com.nhattung.wogo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequestDTO {

    @NotBlank(message = "FULLNAME_NOT_BLANK_MESSAGE")
    private String fullName;

    private String avatarUrl;

    @NotBlank(message = "EMPTY_PASSWORD")
    @Size(min = 6, max = 20, message = "PASSWORD_LENGTH")
    private String password;

    private boolean isActive;
}
