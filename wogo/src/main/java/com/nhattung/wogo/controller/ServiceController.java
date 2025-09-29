package com.nhattung.wogo.controller;

import com.nhattung.wogo.constants.WogoConstants;
import com.nhattung.wogo.dto.request.EstimatedPriceRequestDTO;
import com.nhattung.wogo.dto.request.ServiceRequestDTO;
import com.nhattung.wogo.dto.response.*;
import com.nhattung.wogo.service.serviceWG.IServiceService;
import com.nhattung.wogo.service.serviceWG.suggest.ISuggestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/services")
public class ServiceController {

    private final IServiceService serviceService;
    private final ISuggestService suggestService;

    @GetMapping("/all")
    public ApiResponse<List<ServiceResponseDTO>> getAllServices(
    ) {
        return ApiResponse.<List<ServiceResponseDTO>>builder()
                .result(serviceService.getAllServices())
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
    public ApiResponse<List<ParentServiceResponseDTO>> searchServicesByName(
            @RequestParam String name
    ) {
        return ApiResponse.<List<ParentServiceResponseDTO>>builder()
                .result(serviceService.searchByServiceName(name))
                .message("Search services by name successfully")
                .build();
    }

    @GetMapping("/parent-unique")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<List<ServiceResponseDTO>> getServicesParentUnique() {
        return ApiResponse.<List<ServiceResponseDTO>>builder()
                .result(serviceService.getServicesParentUnique())
                .message("Get parent unique services successfully")
                .build();
    }

    @GetMapping("/child-by-parent/{parentId}")
    public ApiResponse<List<ServiceResponseDTO>> getListChildServiceByParentId(@PathVariable Long parentId) {
        return ApiResponse.<List<ServiceResponseDTO>>builder()
                .result(serviceService.getListChildServiceByParentId(parentId))
                .message("Get child services by parent ID successfully")
                .build();
    }

    @GetMapping("/suggestions/{serviceId}")
    public ApiResponse<EstimatedResponseDTO> getServiceSuggestions(@PathVariable Long serviceId) {
        return ApiResponse.<EstimatedResponseDTO>builder()
                .result(suggestService.suggestPrice(EstimatedPriceRequestDTO.builder()
                        .serviceId(serviceId)
                        .distanceKm(WogoConstants.DEFAULT_DISTANCE_KM)
                        .build()))
                .message("Get service suggestions successfully")
                .build();
    }
}
