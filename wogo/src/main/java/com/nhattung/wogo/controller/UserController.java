package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.request.UserRequestDTO;
import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.UserResponseDTO;
import com.nhattung.wogo.service.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final IUserService userService;

    @GetMapping("/all")
    public ApiResponse<PageResponse<UserResponseDTO>> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<PageResponse<UserResponseDTO>>builder()
                .message("Fetched all users successfully")
                .result(userService.getAllUsers(page, size))
                .build();
    }


    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<UserResponseDTO> updateUser(
            @Valid @ModelAttribute UserRequestDTO user,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) {
        return ApiResponse.<UserResponseDTO>builder()
                .message("User updated successfully")
                .result(userService.updateUser(user,avatar))
                .build();
    }
}
