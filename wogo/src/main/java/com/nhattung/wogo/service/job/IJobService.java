package com.nhattung.wogo.service.job;

import com.nhattung.wogo.dto.request.CreateJobRequestDTO;
import com.nhattung.wogo.dto.response.JobResponseDTO;
import com.nhattung.wogo.dto.response.JobSummaryResponseDTO;
import com.nhattung.wogo.entity.Job;
import com.nhattung.wogo.enums.ActorType;
import com.nhattung.wogo.enums.JobRequestStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IJobService {
    JobResponseDTO saveJob(CreateJobRequestDTO request, List<MultipartFile> files);
    JobResponseDTO getJobByJobRequestCode(String jobRequestCode);
    Job getJobByJobRequestCodeEntity(String jobRequestCode);
    List<JobResponseDTO> getJobsByUserIdByStatus(JobRequestStatus status);
    List<JobSummaryResponseDTO> getJobsByServiceId(Long serviceId);
    void updateStatusAcceptJob(String jobRequestCode,Long workerId);
    void updateStatusCancelJob(String reason, ActorType canceller, String jobRequestCode);
    void cancelJobByJobRequestCode(String reason, String jobRequestCode);
}
