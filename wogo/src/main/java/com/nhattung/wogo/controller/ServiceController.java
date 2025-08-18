package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.request.ServiceRequestDTO;
import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.ParentServiceResponseDTO;
import com.nhattung.wogo.dto.response.ServiceResponseDTO;
import com.nhattung.wogo.service.service.IServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/services")
public class ServiceController {

    private final IServiceService serviceService;


    @GetMapping("/all")
    public ApiResponse<PageResponse<ServiceResponseDTO>> getAllServices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<PageResponse<ServiceResponseDTO>>builder()
                .result(serviceService.getAllServices(page, size))
                .message("Get all services successfully")
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ServiceResponseDTO> getServiceById(@PathVariable Long id) {
        return ApiResponse.<ServiceResponseDTO>builder()
                .result(serviceService.getServiceById(id))
                .message("Get service by ID successfully")
                .build();
    }

    @PostMapping(value = "/save",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<ServiceResponseDTO> saveService(@ModelAttribute ServiceRequestDTO request,
                                                       @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        return ApiResponse.<ServiceResponseDTO>builder()
                .result(serviceService.saveService(request,imageFile))
                .message("Service saved successfully")
                .build();
    }

    @PutMapping(value = "/update/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<ServiceResponseDTO> updateService(@PathVariable Long id,
                                                         @RequestBody ServiceRequestDTO request,
                                                         @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        return ApiResponse.<ServiceResponseDTO>builder()
                .result(serviceService.updateService(id, request,imageFile))
                .message("Service updated successfully")
                .build();
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<Void> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ApiResponse.<Void>builder()
                .message("Service deleted successfully")
                .result(null)
                .build();
    }

    @GetMapping("/searchByName")
    public ApiResponse<PageResponse<ParentServiceResponseDTO>> searchServicesByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<PageResponse<ParentServiceResponseDTO>>builder()
                .result(serviceService.searchByServiceName(name, page, size))
                .message("Search services by name successfully")
                .build();
    }
}
