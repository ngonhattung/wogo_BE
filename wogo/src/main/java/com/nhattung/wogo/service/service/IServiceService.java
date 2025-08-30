package com.nhattung.wogo.service.service;

import com.nhattung.wogo.dto.request.ServiceRequestDTO;
import com.nhattung.wogo.dto.response.*;
import com.nhattung.wogo.entity.ServiceWG;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IServiceService {

    ServiceResponseDTO saveService(ServiceRequestDTO request, MultipartFile imageFile);
    ServiceResponseDTO updateService(Long id, ServiceRequestDTO request, MultipartFile imageFile);
    void deleteService(Long id);
    ServiceResponseDTO getServiceById(Long id);
    ServiceWG getServiceByIdEntity(Long id);
    List<OptionResponseDTO> getServiceOptions();
    List<ServiceResponseDTO> getAllServices();
    List<ParentServiceResponseDTO> searchByServiceName(String serviceName);

}
