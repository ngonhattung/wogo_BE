package com.nhattung.wogo.service.servicecategory;

import com.nhattung.wogo.dto.request.ServiceCategoryRequestDTO;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.ServiceCategoryResponseDTO;

import java.util.List;

public interface IServiceCategoryService {

    //CRUD + Search
    ServiceCategoryResponseDTO saveCategory(ServiceCategoryRequestDTO request);
    ServiceCategoryResponseDTO updateCategory(ServiceCategoryRequestDTO request, Long id);
    void deleteCategory(Long id);
    List<String> getAllServiceCategoryNames();
    ServiceCategoryResponseDTO getCategoryById(Long id);
    PageResponse<ServiceCategoryResponseDTO> getAllServiceCategories(int page, int size);
    PageResponse<ServiceCategoryResponseDTO> searchServiceCategoriesByName(String name, int page, int size);
}
