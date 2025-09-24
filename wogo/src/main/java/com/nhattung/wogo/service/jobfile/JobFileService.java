package com.nhattung.wogo.service.jobfile;

import com.nhattung.wogo.dto.response.UploadS3Response;
import com.nhattung.wogo.entity.Job;
import com.nhattung.wogo.entity.JobFile;
import com.nhattung.wogo.entity.WorkerDocumentFile;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.JobFileRepository;
import com.nhattung.wogo.utils.UploadToS3;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobFileService implements IJobFileService {

    private final JobFileRepository jobFileRepository;
    private final UploadToS3 uploadToS3;

    @Override
    public void saveJobFile(Job job, List<MultipartFile> files) {
        if(files == null || files.isEmpty()) {
            return; // No files to process
        }
        for (MultipartFile file : files) {
            try {
                // Upload to S3
                UploadS3Response s3Response = uploadToS3.uploadFileToS3(file);

                // Save to database
                jobFileRepository.save(
                        JobFile.builder()
                                .job(job)
                                .fileName(s3Response.getFileName())
                                .fileType(s3Response.getFileType())
                                .fileUrl(s3Response.getFileUrl())
                                .build()
                );
            } catch (Exception e) {
                // Log the error and throw a custom exception
                e.printStackTrace();
                throw new AppException(ErrorCode.UPLOAD_IMAGE_ERROR);
            }
        }
    }

    @Override
    public List<JobFile> getJobFilesByJobId(Long jobId) {
        return jobFileRepository.findByJobId(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_FILE_NOT_FOUND));
    }
}
