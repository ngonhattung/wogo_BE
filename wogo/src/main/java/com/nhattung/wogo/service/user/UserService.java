package com.nhattung.wogo.service.user;

import com.nhattung.wogo.dto.request.RegisterRequestDTO;
import com.nhattung.wogo.dto.request.UserRequestDTO;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.ServiceCategoryResponseDTO;
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
import com.nhattung.wogo.utils.SecurityUtils;
import com.nhattung.wogo.utils.UploadToS3;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UploadToS3 uploadToS3;
    @Override
    public UserResponseDTO createUser(RegisterRequestDTO request) {
        return Optional.of(request)
                .filter(user -> !userRepository.existsByPhone(user.getPhone()))
                .map(req -> {

                    User user = User.builder()
                            .phone(request.getPhone())
                            .password(passwordEncoder.encode(request.getPassword()))
                            .isActive(true)
                            .avatarUrl(request.getAvatarUrl())
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

    @Override
    public UserResponseDTO getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(this::convertUserToDto)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public PageResponse<UserResponseDTO> getAllUsers(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new AppException(ErrorCode.INVALID_PAGE_SIZE);
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<User> userPage = userRepository.findAll(pageable);
        List<UserResponseDTO> userResponseDTOS = userPage.getContent().stream()
                .map(this::convertUserToDto)
                .toList();

        return PageResponse.<UserResponseDTO>builder()
                .currentPage(page)
                .totalPages(userPage.getTotalPages())
                .totalElements(userPage.getTotalElements())
                .pageSize(userPage.getSize())
                .data(userResponseDTOS)
                .build();
    }

    @Override
    public UserResponseDTO updateUser(UserRequestDTO user, MultipartFile avatar) {

        User existingUser = userRepository.findById(SecurityUtils.getCurrentUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String avatarUrl = avatar != null && !avatar.isEmpty()
                ? uploadToS3.uploadFileToS3(avatar)
                : existingUser.getAvatarUrl();

        existingUser.setAvatarUrl(avatarUrl);
        existingUser.setFullName(user.getFullName());
        existingUser.setActive(user.isActive());

        // If password is provided, encode and set it
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        User updatedUser = userRepository.save(existingUser);
        return convertUserToDto(updatedUser);

    }

    public UserResponseDTO convertUserToDto(User user) {
        return modelMapper.map(user, UserResponseDTO.class);
    }
}
