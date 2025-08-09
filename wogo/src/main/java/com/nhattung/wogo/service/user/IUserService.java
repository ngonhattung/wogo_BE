package com.nhattung.wogo.service.user;

import com.nhattung.wogo.dto.request.RegisterRequestDTO;
import com.nhattung.wogo.dto.request.UserRequestDTO;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.UserResponseDTO;

public interface IUserService {
    UserResponseDTO createUser(RegisterRequestDTO request);
    UserResponseDTO getUserById(Long userId);
    PageResponse<UserResponseDTO> getAllUsers(int page, int size);
    UserResponseDTO updateUser(UserRequestDTO user, Long id);
}
