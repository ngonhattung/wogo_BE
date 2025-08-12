package com.nhattung.wogo.service.service;

import com.nhattung.wogo.dto.request.ServiceRequestDTO;
import com.nhattung.wogo.dto.response.*;
import com.nhattung.wogo.entity.ServiceWG;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.ServiceRepository;
import com.nhattung.wogo.utils.UploadToS3;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ServiceService implements IServiceService{

    private final ServiceRepository serviceRepository;
    private final ModelMapper modelMapper;
    private final UploadToS3 uploadToS3;
    @Override
    public ServiceResponseDTO saveService(ServiceRequestDTO request, MultipartFile imageFile) {
        if (serviceRepository.existsByServiceName(request.getServiceName())) {
            throw new AppException(ErrorCode.SERVICE_NAME_EXISTS);
        }

        ServiceWG savedService = serviceRepository.save(createService(request, imageFile));

        return convertToResponseDTO(savedService);
    }


    private ServiceWG createService(ServiceRequestDTO request, MultipartFile imageFile) {

        String iconUrl = imageFile != null && !imageFile.isEmpty()
                ? uploadToS3.uploadFileToS3(imageFile)
                : null;

        return ServiceWG.builder()
                .serviceName(request.getServiceName())
                .description(request.getDescription())
                .iconUrl(iconUrl)
                .parentId(request.getParentId())
                .isActive(true)
                .build();
    }

    @Override
    public ServiceResponseDTO updateService(Long id, ServiceRequestDTO request, MultipartFile imageFile) {
        ServiceWG existingService = serviceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

        ServiceWG updatedService = updateExistingService(existingService, request, imageFile);

        return convertToResponseDTO(serviceRepository.save(updatedService));
    }

    private ServiceWG updateExistingService(ServiceWG existingService, ServiceRequestDTO request, MultipartFile imageFile) {
        String iconUrl = imageFile != null && !imageFile.isEmpty()
                ? uploadToS3.uploadFileToS3(imageFile)
                : null;

        existingService.setServiceName(request.getServiceName());
        existingService.setDescription(request.getDescription());
        existingService.setActive(request.getIsActive());
        existingService.setIconUrl(iconUrl);
        existingService.setParentId(request.getParentId());
        return existingService;
    }

    @Override
    public void deleteService(Long id) {
        serviceRepository.findById(id)
                .ifPresentOrElse(
                        service -> {
                            service.setActive(false);
                            serviceRepository.save(service);
                        },
                        () -> {
                            throw new AppException(ErrorCode.SERVICE_NOT_FOUND);
                        }
                );
    }

    @Override
    public ServiceResponseDTO getServiceById(Long id) {
        return serviceRepository.findById(id)
                .map(this::convertToResponseDTO)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));
    }

    @Override
    public ServiceWG getServiceByIdEntity(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));
    }

    @Override
    public List<OptionResponseDTO> getServiceOptions() {
        List<ServiceWG> services = serviceRepository.findAllByIsActiveTrueAndParentIdIsNull();
        return services.stream()
                .map(service -> OptionResponseDTO.builder()
                        .value(String.valueOf(service.getId()))
                        .label(service.getServiceName())
                        .build())
                .toList();
    }

    @Override
    public PageResponse<ServiceResponseDTO> getAllServices(int page, int size) {
        if( page < 0 || size <= 0) {
            throw new AppException(ErrorCode.INVALID_PAGE_SIZE);
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<ServiceWG> servicePage = serviceRepository.findAll(pageable);
        List<ServiceResponseDTO> serviceResponseList = servicePage.stream()
                .map(this::convertToResponseDTO)
                .toList();

        return PageResponse.<ServiceResponseDTO>builder()
                .currentPage(page)
                .totalPages(servicePage.getTotalPages())
                .totalElements(servicePage.getTotalElements())
                .pageSize(servicePage.getSize())
                .data(serviceResponseList)
                .build();

    }

    @Override
    public PageResponse<ParentServiceResponseDTO> searchByServiceName(String serviceName, int page, int size) {
        if (page < 0 || size <= 0) {
            throw new AppException(ErrorCode.INVALID_PAGE_SIZE);
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<ServiceWG> servicePage = serviceRepository.searchByServiceOrParentName(serviceName, pageable);
        List<ServiceWG> services = servicePage.getContent();

        List<ParentServiceResponseDTO> grouped = services.stream()
                .filter(c -> c.getParentId() == null)
                .map(parent -> {
                    // Tìm con của cha này
                    List<ServiceResponseDTO> children = services.stream()
                            .filter(child -> Objects.equals(child.getParentId(), parent.getId()))
                            .map(this::convertToResponseDTO)
                            .toList();

                    return ParentServiceResponseDTO.builder()
                            .parentService(convertToResponseDTO(parent))
                            .childServices(children)
                            .build();
                }).toList();

        return PageResponse.<ParentServiceResponseDTO>builder()
                .currentPage(page)
                .totalPages(servicePage.getTotalPages())
                .totalElements(servicePage.getTotalElements())
                .pageSize(servicePage.getSize())
                .data(grouped)
                .build();
    }


    private ServiceResponseDTO convertToResponseDTO(ServiceWG service) {
        return modelMapper.map(service, ServiceResponseDTO.class);
    }
}
