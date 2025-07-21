package com.nhattung.wogo.service.auth;

import com.nhattung.wogo.dto.request.RegisterRequestDTO;
import com.nhattung.wogo.dto.response.UserResponseDTO;

public interface IUserService {
    UserResponseDTO createUser(RegisterRequestDTO request);
}
