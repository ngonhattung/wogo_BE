package com.nhattung.wogo.service.suggest;

import com.nhattung.wogo.dto.request.EstimatedPriceRequestDTO;
import com.nhattung.wogo.dto.response.EstimatedApiResponseDTO;
import com.nhattung.wogo.dto.response.EstimatedResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class SuggestService implements ISuggestService{

    private final RestTemplate restTemplate;

    @Value("${suggest.url}")
    private String suggestUrl;


    @Override
    public EstimatedResponseDTO suggestPrice(EstimatedPriceRequestDTO request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EstimatedPriceRequestDTO> entity = new HttpEntity<>(request, headers);

        ResponseEntity<EstimatedApiResponseDTO> response =
                restTemplate.exchange(suggestUrl, HttpMethod.POST, entity, EstimatedApiResponseDTO.class);

        EstimatedApiResponseDTO apiResponse = response.getBody();

        assert apiResponse != null;
        return EstimatedResponseDTO.builder()
                .estimatedPriceLower(apiResponse.getRange().getLow())
                .estimatedPriceHigher(apiResponse.getRange().getHigh())
                .estimatedDurationMinutes(apiResponse.getDurationMinutes())
                .build();
    }
}
