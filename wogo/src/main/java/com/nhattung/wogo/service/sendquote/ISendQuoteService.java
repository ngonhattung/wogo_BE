package com.nhattung.wogo.service.sendquote;

import com.nhattung.wogo.dto.request.CreateSendQuoteRequestDTO;
import com.nhattung.wogo.dto.request.SendQuoteRequestDTO;
import com.nhattung.wogo.dto.response.WorkerQuoteResponseDTO;
import com.nhattung.wogo.enums.JobRequestStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface ISendQuoteService {

    WorkerQuoteResponseDTO saveSendQuote(CreateSendQuoteRequestDTO request);
    List<WorkerQuoteResponseDTO> getSendQuotesByWorkerId(JobRequestStatus status);
    boolean checkExistSendQuote(Long serviceId, Long workerId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    List<WorkerQuoteResponseDTO> getSendQuotesByJobRequestCode(String jobRequestCode);
}
