package com.nhattung.wogo.dto.request;

import lombok.Data;

@Data
public class RegisterRequestDTO {

    private String phone;
    private String password;
    private String fullName;
    private String avatarUrl;
}
