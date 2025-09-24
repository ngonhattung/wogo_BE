package com.nhattung.wogo.service.job;

import com.nhattung.wogo.dto.request.CreateJobRequestDTO;
import com.nhattung.wogo.dto.request.FindServiceRequestDTO;
import com.nhattung.wogo.dto.response.*;
import com.nhattung.wogo.entity.Job;
import com.nhattung.wogo.entity.JobFile;
import com.nhattung.wogo.entity.ServiceWG;
import com.nhattung.wogo.entity.User;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.enums.JobRequestStatus;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.JobRepository;
import com.nhattung.wogo.service.jobfile.JobFileService;
import com.nhattung.wogo.service.service.IServiceService;
import com.nhattung.wogo.service.user.IUserService;
import com.nhattung.wogo.utils.SecurityUtils;
import com.nhattung.wogo.utils.UploadToS3;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobService implements IJobService {

    private final JobRepository jobRepository;
    private final IServiceService serviceService;
    private final IUserService userService;
    private final ModelMapper modelMapper;
    private final JobFileService jobFileService;

    @Override
    public JobResponseDTO saveJob(CreateJobRequestDTO request, List<MultipartFile> files) {

        Long userId = SecurityUtils.getCurrentUserId();
        ServiceWG service = serviceService.getServiceByIdEntity(request.getServiceId());
        User user = userService.getUserByIdEntity(userId);

        Job job = createJob(request, service, user);
        jobFileService.saveJobFile(job, files);
        Job savedJob = jobRepository.save(job);

        return convertToResponseDTO(savedJob);

    }

    private Job createJob(CreateJobRequestDTO request, ServiceWG service, User user) {
        return Job.builder()
                .service(service)
                .user(user)
                .jobRequestCode(generateJobRequestCode())
                .description(request.getDescription())
                .distance(0) // Default distance, to be updated later
                .bookingDate(request.getBookingDate())
                .status(JobRequestStatus.PENDING)
                .bookingAddress(request.getAddress())
                .estimatedPriceLower(request.getEstimatedPriceLower())
                .estimatedPriceHigher(request.getEstimatedPriceHigher())
                .estimatedDurationMinutes(request.getEstimatedDurationMinutes())
                .build();
    }

    private String generateJobRequestCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return "JR-" + uuid.substring(0, 8) + "-" + LocalDateTime.now().getYear();
    }

    @Override
    public JobResponseDTO getJobByJobRequestCode(String jobRequestCode) {
        return jobRepository.findValidJobByJobRequestCode(jobRequestCode,LocalDateTime.now())
                .map(this::convertToResponseDTO)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));
    }

    @Override
    public Job getJobByJobRequestCodeEntity(String jobRequestCode) {
        return jobRepository.findValidJobByJobRequestCode(jobRequestCode,LocalDateTime.now())
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));
    }

    @Override
    public List<JobResponseDTO> getJobsByUserId() {
        return jobRepository.findValidJobsByUserId(SecurityUtils.getCurrentUserId(),LocalDateTime.now())
                .orElse(new ArrayList<>())
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    public List<JobResponseDTO> getJobsByServiceId(Long serviceId) {
        return jobRepository.getValidJobsByServiceId(serviceId,JobRequestStatus.PENDING, LocalDateTime.now())
                .orElse(new ArrayList<>())
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    public void updateStatusAcceptJob(String jobRequestCode,Long workerId) {
        Job job = jobRepository.findValidJobByJobRequestCode(jobRequestCode,LocalDateTime.now())
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));
        job.setStatus(JobRequestStatus.ACCEPTED);
        job.setAcceptedBy(workerId);
        jobRepository.save(job);
    }

    @Override
    public void deleteJob(String jobRequestCode) {
        Job job = jobRepository.findValidJobByJobRequestCode(jobRequestCode,LocalDateTime.now())
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));
        jobRepository.delete(job);
    }

    private JobResponseDTO convertToResponseDTO(Job job) {
        JobResponseDTO responseDTO = modelMapper.map(job, JobResponseDTO.class);
        UserResponseDTO userResponseDTO = modelMapper.map(job.getUser(), UserResponseDTO.class);
        ServiceResponseDTO serviceResponseDTO = modelMapper.map(job.getService(), ServiceResponseDTO.class);
        responseDTO.setUser(userResponseDTO);
        responseDTO.setService(serviceResponseDTO);

        List<JobFile> jobFiles = jobFileService.getJobFilesByJobId(job.getId());
        List<JobFileResponseDTO> filesDTO = jobFiles
                .stream()
                .map(jobFile -> modelMapper.map(jobFile, JobFileResponseDTO.class))
                .toList();
        responseDTO.setFiles(filesDTO);
        return responseDTO;
    }
}
