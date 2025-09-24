package com.nhattung.wogo.service.job;

import com.nhattung.wogo.dto.request.CreateJobRequestDTO;
import com.nhattung.wogo.dto.request.FindServiceRequestDTO;
import com.nhattung.wogo.dto.response.JobResponseDTO;
import com.nhattung.wogo.entity.Job;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IJobService {
    JobResponseDTO saveJob(CreateJobRequestDTO request, List<MultipartFile> files);
    JobResponseDTO getJobByJobRequestCode(String jobRequestCode);
    Job getJobByJobRequestCodeEntity(String jobRequestCode);
    List<JobResponseDTO> getJobsByUserId();
    List<JobResponseDTO> getJobsByServiceId(Long serviceId);
    void updateStatusAcceptJob(String jobRequestCode,Long workerId);
    void deleteJob(String jobRequestCode);
}
