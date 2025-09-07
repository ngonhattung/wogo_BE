package com.nhattung.wogo.service.service;

import com.nhattung.wogo.dto.request.ServiceRequestDTO;
import com.nhattung.wogo.dto.response.*;
import com.nhattung.wogo.entity.ServiceWG;
import com.nhattung.wogo.entity.WorkerService;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.ServiceRepository;
import com.nhattung.wogo.service.worker.IWorkerService;
import com.nhattung.wogo.service.workerservice.IWorkerServiceService;
import com.nhattung.wogo.utils.SecurityUtils;
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
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ServiceService implements IServiceService{

    private final ServiceRepository serviceRepository;
    private final ModelMapper modelMapper;
    private final UploadToS3 uploadToS3;
    private final IWorkerServiceService workerServiceService;
    @Override
    public ServiceResponseDTO saveService(ServiceRequestDTO request, MultipartFile imageFile) {
        if (serviceRepository.existsByServiceName(request.getServiceName())) {
            throw new AppException(ErrorCode.SERVICE_NAME_EXISTS);
        }

        ServiceWG savedService = serviceRepository.save(createService(request, imageFile));

        return convertToResponseDTO(savedService);
    }


    private ServiceWG createService(ServiceRequestDTO request, MultipartFile imageFile) {

        String iconUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            UploadS3Response uploadResponse = uploadToS3.uploadFileToS3(imageFile);
            iconUrl = uploadResponse != null ? uploadResponse.getFileUrl() : null;
        }

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
        String iconUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            UploadS3Response uploadResponse = uploadToS3.uploadFileToS3(imageFile);
            iconUrl = uploadResponse != null ? uploadResponse.getFileUrl() : null;
        }

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
    public List<ServiceResponseDTO> getAllServices() {
        return serviceRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

    }

    @Override
    public List<ParentServiceResponseDTO> searchByServiceName(String serviceName) {
        // Tìm tất cả service phù hợp
        List<ServiceWG> services = (serviceName == null || serviceName.trim().isEmpty())
                ? serviceRepository.findAll()
                : serviceRepository.searchByServiceOrParentName(serviceName);

        // Lọc ra cha và build response bằng hàm chung
        return services.stream()
                .filter(c -> c.getParentId() == null)
                .map(parent -> buildParentServiceDTO(parent, services))
                .toList();
    }

    @Override
    public List<WorkerServiceResponseDTO> getAllServicesOfWorker() {
        List<WorkerService> workerServices = workerServiceService.getWorkerServicesByUserId();
        return workerServices.stream()
                .map(this::convertToResponseDTOWithService)
                .toList();
    }

    @Override
    public List<Long> getAllWorkerChildServiceIdsByWorkerId() {
        return getAllServicesOfWorker().stream()
                .flatMap(dto -> {
                    List<ServiceResponseDTO> children = dto.getService().getChildServices();
                    if (children == null || children.isEmpty()) {
                        // Không có child → lấy luôn parent
                        return Stream.of(dto.getService().getParentService());
                    }
                    return children.stream();
                })
                .map(ServiceResponseDTO::getId)
                .toList();
    }


    private WorkerServiceResponseDTO convertToResponseDTOWithService(WorkerService workerService) {
        WorkerServiceResponseDTO dto = workerServiceService.convertToResponseDTO(workerService);

        // Lấy service chính mà worker này cung cấp
        ServiceWG service = workerService.getService();

        // Trường hợp service là con → lấy cha nó
        ServiceWG parent = (service.getParentId() == null)
                ? service
                : serviceRepository.findById(service.getParentId())
                .orElse(service);

        // Lấy toàn bộ service liên quan để build cha + con
        List<ServiceWG> relatedServices =  serviceRepository.findByParentIdOrId(parent.getId(), parent.getId());

        // Reuse hàm build chung
        ParentServiceResponseDTO parentDTO = buildParentServiceDTO(parent, relatedServices);

        dto.setService(parentDTO);
        return dto;
    }

    private ParentServiceResponseDTO buildParentServiceDTO(ServiceWG parent, List<ServiceWG> allServices) {
        List<ServiceResponseDTO> children = allServices.stream()
                .filter(child -> Objects.equals(child.getParentId(), parent.getId()))
                .map(this::convertToResponseDTO)
                .toList();

        return ParentServiceResponseDTO.builder()
                .parentService(convertToResponseDTO(parent))
                .childServices(children)
                .build();
    }

    private ServiceResponseDTO convertToResponseDTO(ServiceWG service) {
        return modelMapper.map(service, ServiceResponseDTO.class);
    }
}
