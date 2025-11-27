package com.nhattung.wogo.service.user;

import com.nhattung.wogo.dto.request.RegisterRequestDTO;
import com.nhattung.wogo.dto.request.UserRequestDTO;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.UserRegistrationStatsResponseDTO;
import com.nhattung.wogo.dto.response.UserResponseDTO;
import com.nhattung.wogo.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IUserService {
    UserResponseDTO createUser(RegisterRequestDTO request);
    UserResponseDTO getUserById(Long userId);
    User getUserByIdEntity(Long userId);
    PageResponse<UserResponseDTO> getAllUsers(int page, int size);
    UserResponseDTO updateUser(UserRequestDTO user, MultipartFile avatar);
    User getCurrentUser();
    //add userRole for user
    void addUserRole(Long userId, String roleName);
    boolean isExistRole(Long userId, String roleName);
    boolean isPhoneExist(String phone);
    boolean updatePasswordByPhone(String phone, String newPassword);
    List<UserResponseDTO> searchUsersByPhone(String phone);
    int countTotalCustomers();
    long countTotalUsers();

    //lay tong so luong nguoi dang ky theo thang
    List<UserRegistrationStatsResponseDTO> countRegisteredUsersByYear(int year);
}
