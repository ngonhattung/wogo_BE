package com.nhattung.wogo.service.job;

import com.nhattung.wogo.dto.request.CreateJobRequestDTO;
import com.nhattung.wogo.dto.response.*;
import com.nhattung.wogo.entity.Job;
import com.nhattung.wogo.entity.JobFile;
import com.nhattung.wogo.entity.ServiceWG;
import com.nhattung.wogo.entity.User;
import com.nhattung.wogo.enums.Canceller;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.enums.JobRequestStatus;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.JobRepository;
import com.nhattung.wogo.service.job.file.JobFileService;
import com.nhattung.wogo.service.serviceWG.IServiceService;
import com.nhattung.wogo.service.user.IUserService;
import com.nhattung.wogo.utils.SecurityUtils;
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

        ServiceWG service = serviceService.getServiceByIdEntity(request.getServiceId());
        Job job = createJob(request, service);
        jobFileService.saveJobFile(job, files);
        Job savedJob = jobRepository.save(job);

        return convertToResponseDTO(savedJob);

    }

    private Job createJob(CreateJobRequestDTO request, ServiceWG service) {
        return Job.builder()
                .service(service)
                .user(userService.getCurrentUser())
                .jobRequestCode(generateJobRequestCode())
                .description(request.getDescription())
                .bookingDate(request.getBookingDate())
                .status(JobRequestStatus.PENDING)
                .bookingAddress(request.getAddress())
                .estimatedPriceLower(request.getEstimatedPriceLower())
                .estimatedPriceHigher(request.getEstimatedPriceHigher())
                .estimatedDurationMinutes(request.getEstimatedDurationMinutes())
                .longitude(request.getLongitudeUser())
                .latitude(request.getLatitudeUser())
                .build();
    }

    private String generateJobRequestCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return "JR" + uuid.substring(0, 8) + LocalDateTime.now().getYear();
    }

    @Override
    public JobResponseDTO getJobByJobRequestCode(String jobRequestCode) {
        return jobRepository.findByJobRequestCode(jobRequestCode)
                .map(this::convertToResponseDTO)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));
    }

    @Override
    public Job getJobByJobRequestCodeEntity(String jobRequestCode) {
        return jobRepository.findByJobRequestCode(jobRequestCode)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));
    }

    @Override
    public List<JobResponseDTO> getJobsByUserIdByStatus(JobRequestStatus status) {
        return jobRepository.findValidJobsByUserId(SecurityUtils.getCurrentUserId(),status)
                .orElse(new ArrayList<>())
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    public List<JobSummaryResponseDTO> getJobsByServiceId(Long serviceId) {
        return jobRepository.getValidJobsByServiceId(serviceId,JobRequestStatus.PENDING, LocalDateTime.now())
                .orElse(new ArrayList<>())
                .stream()
                .map(this::convertToResponseDTOSummary)
                .toList();
    }

    @Override
    public void updateStatusAcceptJob(String jobRequestCode,Long workerId) {
        Job job = jobRepository.findByJobRequestCode(jobRequestCode)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));
        job.setStatus(JobRequestStatus.ACCEPTED);
        job.setAcceptedBy(workerId);
        jobRepository.save(job);
    }

    @Override
    public void updateStatusCancelJob() {
        //Lấy ra danh sách hết hạn chưa có thợ nhận
        List<Job> jobs = jobRepository.findJobsToCancel(JobRequestStatus.PENDING,LocalDateTime.now())
                .orElse(new ArrayList<>());
        jobRepository.saveAll(jobs.stream().peek(job -> {
            job.setStatus(JobRequestStatus.CANCELLED);
            job.setCancelledBy(Canceller.SYSTEM);
            job.setCancelReason("Hủy sau 24h và không tìm được thợ");
        }).toList());
    }


    private <T extends JobBaseResponseDTO> T convertToResponse(Job job, Class<T> targetType) {
        T responseDTO = modelMapper.map(job, targetType);

        UserResponseDTO userResponseDTO = modelMapper.map(job.getUser(), UserResponseDTO.class);
        ServiceResponseDTO serviceResponseDTO = modelMapper.map(job.getService(), ServiceResponseDTO.class);

        responseDTO.setUser(userResponseDTO);
        responseDTO.setService(serviceResponseDTO);

        List<JobFile> jobFiles = jobFileService.getJobFilesByJobId(job.getId());
        List<JobFileResponseDTO> filesDTO = jobFiles.stream()
                .map(jobFile -> modelMapper.map(jobFile, JobFileResponseDTO.class))
                .toList();

        responseDTO.setFiles(filesDTO);

        return responseDTO;
    }

    // Gọi dùng cho từng trường hợp
    private JobResponseDTO convertToResponseDTO(Job job) {
        return convertToResponse(job, JobResponseDTO.class);
    }

    private JobSummaryResponseDTO convertToResponseDTOSummary(Job job) {
        return convertToResponse(job, JobSummaryResponseDTO.class);
    }
}
