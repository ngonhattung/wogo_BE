package com.nhattung.wogo.dto.request;


import lombok.Data;

@Data
public class LoginRequestDTO {
    private String phone;
    private String password;
}
