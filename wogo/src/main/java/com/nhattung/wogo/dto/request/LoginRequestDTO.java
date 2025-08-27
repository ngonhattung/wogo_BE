package com.nhattung.wogo.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotBlank(message = "EMPTY_PHONE")
//    @Pattern(
//            regexp = "^(0|\\+84)(\\d{9})$",
//            message = "PHONE_INVALID"
//    )
    private String phone;

    @NotBlank(message = "EMPTY_PASSWORD")
//    @Size(min = 6, max = 20, message = "PASSWORD_LENGTH")
    private String password;
}