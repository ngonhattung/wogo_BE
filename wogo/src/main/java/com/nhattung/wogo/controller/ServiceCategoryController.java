package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.request.ServiceCategoryRequestDTO;
import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.ServiceCategoryResponseDTO;
import com.nhattung.wogo.service.servicecategory.IServiceCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/service-categories")
@RequiredArgsConstructor
public class ServiceCategoryController {

    private final IServiceCategoryService serviceCategoryService;

    @PostMapping("/save")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<ServiceCategoryResponseDTO> saveCategory(@RequestBody ServiceCategoryRequestDTO request) {
        return ApiResponse.<ServiceCategoryResponseDTO>builder()
                .message("Service category saved successfully")
                .result(serviceCategoryService.saveCategory(request))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ServiceCategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        return ApiResponse.<ServiceCategoryResponseDTO>builder()
                .message("Fetched service category by ID successfully")
                .result(serviceCategoryService.getCategoryById(id))
                .build();
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<ServiceCategoryResponseDTO> updateCategory(
            @RequestBody ServiceCategoryRequestDTO request, @PathVariable Long id) {
        return ApiResponse.<ServiceCategoryResponseDTO>builder()
                .message("Service category updated successfully")
                .result(serviceCategoryService.updateCategory(request, id))
                .build();
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        serviceCategoryService.deleteCategory(id);
        return ApiResponse.<Void>builder()
                .message("Service category deleted successfully")
                .result(null)
                .build();
    }

    @GetMapping("/all")
    public ApiResponse<PageResponse<ServiceCategoryResponseDTO>> getAllServiceCategories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.<PageResponse<ServiceCategoryResponseDTO>>builder()
                .message("Fetched all service categories successfully")
                .result(serviceCategoryService.getAllServiceCategories(page, size))
                .build();
    }

    //Tìm kiếm danh mục dịch vụ theo tên cho phần thêm nghiệp vụ thợ
    @GetMapping("/searchByName")
    public ApiResponse<PageResponse<ServiceCategoryResponseDTO>> searchServiceCategoriesByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.<PageResponse<ServiceCategoryResponseDTO>>builder()
                .message("Searched service categories by name successfully")
                .result(serviceCategoryService.searchServiceCategoriesByName(name, page, size))
                .build();
    }
}
