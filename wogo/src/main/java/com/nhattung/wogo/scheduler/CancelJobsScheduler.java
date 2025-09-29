package com.nhattung.wogo.scheduler;

import com.nhattung.wogo.service.job.IJobService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CancelJobsScheduler {

    private final IJobService jobService;
    private static final Logger logger = LoggerFactory.getLogger(CancelJobsScheduler.class);

    @Scheduled(cron = "0 0 2 * * ?") // 2h AM every day
    @Transactional
    public void cancelExpiredJobs() {
        try {
            jobService.updateStatusCancelJob();
            logger.info("Cancel expired jobs completed successfully at {}", java.time.LocalDateTime.now());
        } catch (Exception e) {
            logger.error("Error during canceling expired jobs", e);
        }
    }
}
