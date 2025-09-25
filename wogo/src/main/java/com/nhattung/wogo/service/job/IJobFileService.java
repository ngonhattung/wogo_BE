package com.nhattung.wogo.service.job;

import com.nhattung.wogo.entity.Job;
import com.nhattung.wogo.entity.JobFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IJobFileService {
    void saveJobFile(Job job, List<MultipartFile> files);
    List<JobFile> getJobFilesByJobId(Long jobId);
}
