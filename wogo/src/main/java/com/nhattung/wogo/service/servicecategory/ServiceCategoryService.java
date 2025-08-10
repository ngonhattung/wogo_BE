package com.nhattung.wogo.service.servicecategory;


import com.nhattung.wogo.dto.request.ServiceCategoryRequestDTO;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.ParentCategoryResponseDTO;
import com.nhattung.wogo.dto.response.ServiceCategoryResponseDTO;
import com.nhattung.wogo.entity.ServiceCategory;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.ServiceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ServiceCategoryService implements IServiceCategoryService {

    private final ServiceCategoryRepository serviceCategoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public ServiceCategoryResponseDTO saveCategory(ServiceCategoryRequestDTO request) {

        if (isCategoryNameExists(request.getCategoryName())) {
            throw new AppException(ErrorCode.SERVICE_CATEGORY_NAME_EXISTS);
        }

        ServiceCategory savedServiceCategory = serviceCategoryRepository.save(createServiceCategory(request));

        return convertToResponseDTO(savedServiceCategory);
    }

    private ServiceCategory createServiceCategory(ServiceCategoryRequestDTO request) {
        return ServiceCategory.builder()
                .categoryName(request.getCategoryName())
                .description(request.getDescription())
                .parentId(request.getParentId())
                .icon(request.getIcon())
                .isActive(true) // Assuming new categories are active by default
                .build();
    }

    private boolean isCategoryNameExists(String categoryName) {
        return serviceCategoryRepository.existsByCategoryName(categoryName);
    }

    @Override
    public ServiceCategoryResponseDTO updateCategory(ServiceCategoryRequestDTO request, Long id) {
        return serviceCategoryRepository.findById(id)
                .map(existingCategory -> updateExistingCategory(existingCategory, request))
                .map(serviceCategoryRepository::save)
                .map(this::convertToResponseDTO)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_CATEGORY_NOT_FOUND));

    }

    private ServiceCategory updateExistingCategory(ServiceCategory existingCategory, ServiceCategoryRequestDTO request) {
        existingCategory.setCategoryName(request.getCategoryName());
        existingCategory.setDescription(request.getDescription());
        existingCategory.setParentId(request.getParentId());
        existingCategory.setIcon(request.getIcon());
        existingCategory.setActive(request.isActive());
        return existingCategory;
    }

    @Override
    public void deleteCategory(Long id) {
        serviceCategoryRepository.findById(id).ifPresentOrElse(serviceCategoryRepository::delete, () -> {
            throw new AppException(ErrorCode.SERVICE_CATEGORY_NOT_FOUND);
        });
    }

    @Override
    public List<String> getAllServiceCategoryNames() {
        return serviceCategoryRepository.findAll().stream().map(ServiceCategory::getCategoryName).toList();
    }

    @Override
    public ServiceCategoryResponseDTO getCategoryById(Long id) {
        return serviceCategoryRepository.findById(id)
                .map(this::convertToResponseDTO)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_CATEGORY_NOT_FOUND));
    }

    @Override
    public PageResponse<ServiceCategoryResponseDTO> getAllServiceCategories(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new AppException(ErrorCode.INVALID_PAGE_SIZE);
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<ServiceCategory> serviceCategoryPage = serviceCategoryRepository.findAll(pageable);
        List<ServiceCategoryResponseDTO> serviceCategoryResponses = serviceCategoryPage.stream()
                .map(this::convertToResponseDTO)
                .toList();

        return PageResponse.<ServiceCategoryResponseDTO>builder()
                .currentPage(page)
                .totalPages(serviceCategoryPage.getTotalPages())
                .totalElements(serviceCategoryPage.getTotalElements())
                .pageSize(serviceCategoryPage.getSize())
                .data(serviceCategoryResponses)
                .build();

    }

    @Override
    public PageResponse<ParentCategoryResponseDTO> searchServiceCategoriesByName(String name, int page, int size) {
        if (page < 0 || size <= 0) {
            throw new AppException(ErrorCode.INVALID_PAGE_SIZE);
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<ServiceCategory> serviceCategoryPage = serviceCategoryRepository.searchByCategoryOrParentName(name, pageable);
        List<ServiceCategory> allCategories = serviceCategoryPage.getContent();

        // Nhóm theo category cha (parentId null)
        List<ParentCategoryResponseDTO> grouped = allCategories.stream()
                .filter(c -> c.getParentId() == null) // chỉ lấy cha
                .map(parent -> {

                    // Tìm con của cha này
                    List<ServiceCategoryResponseDTO> children = allCategories.stream()
                            .filter(child -> Objects.equals(child.getParentId(), parent.getId()))
                            .map(this::convertToResponseDTO)
                            .toList();

                    return ParentCategoryResponseDTO.builder()
                            .parent(convertToResponseDTO(parent))
                            .children(children)
                            .build();
                })
                .toList();

        return PageResponse.<ParentCategoryResponseDTO>builder()
                .currentPage(page)
                .totalPages(serviceCategoryPage.getTotalPages())
                .totalElements(serviceCategoryPage.getTotalElements())
                .pageSize(serviceCategoryPage.getSize())
                .data(grouped)
                .build();
    }

    private ServiceCategoryResponseDTO convertToResponseDTO(ServiceCategory serviceCategory) {
        return modelMapper.map(serviceCategory, ServiceCategoryResponseDTO.class);
    }
}
