package com.nhattung.wogo.service.auth;

import com.nhattung.wogo.dto.request.RegisterRequestDTO;
import com.nhattung.wogo.dto.response.UserResponseDTO;
import com.nhattung.wogo.entity.Role;
import com.nhattung.wogo.entity.User;
import com.nhattung.wogo.entity.UserRole;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.enums.ROLE;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.RoleRepository;
import com.nhattung.wogo.repository.UserRepository;
import com.nhattung.wogo.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public UserResponseDTO createUser(RegisterRequestDTO request) {
        return Optional.of(request)
                .filter(user -> !userRepository.existsByPhone(user.getPhone()))
                .map(req -> {
                    User user = User.builder()
                            .phone(request.getPhone())
                            .password(passwordEncoder.encode(request.getPassword()))
                            .isActive(true)
                            .fullName(request.getFullName())
                            .build();
                    // Set default role if not provided
                    Role role = roleRepository.findByRoleName(ROLE.CUSTOMER.getValue())
                            .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
                    User savedUser = userRepository.save(user);

                    // Save user role
                    userRoleRepository.save(UserRole.builder()
                            .role(role)
                            .user(savedUser)
                            .isActive(true)
                            .build());

                    // Return the saved user as a DTO
                    return convertUserToDto(savedUser);
                }).orElseThrow(() -> new AppException(ErrorCode.USER_ALREADY_EXISTS));
    }

    public UserResponseDTO convertUserToDto(User user) {
        return modelMapper.map(user, UserResponseDTO.class);
    }
}
