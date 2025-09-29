package com.nhattung.wogo.service.serviceWG.suggest;

import com.nhattung.wogo.dto.request.EstimatedPriceRequestDTO;
import com.nhattung.wogo.dto.response.EstimatedResponseDTO;

public interface ISuggestService {
    EstimatedResponseDTO suggestPrice(EstimatedPriceRequestDTO request);
}
