package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.UserRegistrationStatsResponseDTO;
import com.nhattung.wogo.service.user.IUserService;
import com.nhattung.wogo.service.worker.IWorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final IWorkerService workerService;
    private final IUserService userService;

    //Api tổng so lượng thợ
    @GetMapping("/total-workers")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<Long> getTotalWorkers() {
        return ApiResponse.<Long>builder()
                .message("Fetched total workers successfully")
                .result(workerService.countTotalWorker())
                .build();
    }

    //Api tổng số lượng khách hàng
    @GetMapping("/total-customers")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<Integer> getTotalCustomers() {
        return ApiResponse.<Integer>builder()
                .message("Fetched total customers successfully")
                .result(userService.countTotalCustomers())
                .build();
    }

    //Api tổng users
    @GetMapping("/total-users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<Long> getTotalUsers() {
        return ApiResponse.<Long>builder()
                .message("Fetched total users successfully")
                .result(userService.countTotalUsers())
                .build();
    }

    //Api tổng so luong nguoi dang ky theo thang
    @GetMapping("/registered-users-by-year/{year}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<List<UserRegistrationStatsResponseDTO>> getRegisteredUsersByMonth(@PathVariable int year) {
        return ApiResponse.<List<UserRegistrationStatsResponseDTO>>builder()
                .message("Fetched registered users by month successfully")
                .result(userService.countRegisteredUsersByYear(year))
                .build();
    }
}
