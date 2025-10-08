package com.nhattung.wogo.service.sendquote;

import com.nhattung.wogo.dto.request.CreateSendQuoteRequestDTO;
import com.nhattung.wogo.dto.request.SendQuoteRequestDTO;
import com.nhattung.wogo.dto.response.WorkerQuoteResponseDTO;
import com.nhattung.wogo.entity.Job;
import com.nhattung.wogo.entity.Worker;
import com.nhattung.wogo.entity.WorkerQuote;
import com.nhattung.wogo.enums.JobRequestStatus;
import com.nhattung.wogo.repository.SendQuoteRepository;
import com.nhattung.wogo.service.job.IJobService;
import com.nhattung.wogo.service.worker.IWorkerService;
import com.nhattung.wogo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SendQuoteService implements ISendQuoteService {

    private final SendQuoteRepository sendQuoteRepository;
    private final ModelMapper modelMapper;
    private final IWorkerService workerService;

    @Override
    public WorkerQuoteResponseDTO saveSendQuote(CreateSendQuoteRequestDTO request) {

        Worker worker = workerService.getWorkerByUserId(SecurityUtils.getCurrentUserId());

        return convertToResponseDTO(sendQuoteRepository.save(WorkerQuote.builder()
                .worker(worker)
                .job(request.getJob())
                .quotedPrice(request.getQuotedPrice())
                .distanceToJob(request.getDistanceToJob())
                .build()));
    }

    @Override
    public List<WorkerQuoteResponseDTO> getSendQuotesByWorkerId(JobRequestStatus status){
        return sendQuoteRepository.findByWorkerUserIdAndStatus(SecurityUtils.getCurrentUserId(),status)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    public boolean checkExistSendQuote(Long serviceId, Long workerId, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return sendQuoteRepository.checkExistSendQuote(serviceId, workerId, startOfDay, endOfDay);
    }

    @Override
    public List<WorkerQuoteResponseDTO> getSendQuotesByJobRequestCode(String jobRequestCode) {
        return sendQuoteRepository.findByJobRequestCode(jobRequestCode)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    private WorkerQuoteResponseDTO convertToResponseDTO(WorkerQuote workerQuote) {
        return modelMapper.map(workerQuote, WorkerQuoteResponseDTO.class);
    }
}
