package com.nhattung.wogo.service.sendquote;

import com.nhattung.wogo.dto.request.SendQuoteRequestDTO;
import com.nhattung.wogo.dto.response.WorkerQuoteResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ISendQuoteService {

    WorkerQuoteResponseDTO saveSendQuote(SendQuoteRequestDTO request);
    List<WorkerQuoteResponseDTO> getSendQuotesByWorkerId();
    boolean checkExistSendQuote(Long serviceId, Long workerId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
