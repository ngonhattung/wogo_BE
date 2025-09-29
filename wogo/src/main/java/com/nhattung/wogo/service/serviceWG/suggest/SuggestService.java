package com.nhattung.wogo.service.serviceWG.suggest;

import com.nhattung.wogo.dto.request.EstimatedPriceRequestDTO;
import com.nhattung.wogo.dto.response.EstimatedApiResponseDTO;
import com.nhattung.wogo.dto.response.EstimatedResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

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

        try {
            ResponseEntity<EstimatedApiResponseDTO> response =
                    restTemplate.exchange(suggestUrl, HttpMethod.POST, entity, EstimatedApiResponseDTO.class);

            EstimatedApiResponseDTO apiResponse = response.getBody();

            if (apiResponse == null || apiResponse.getRange() == null) {
                return defaultEstimatedResponse();
            }

            return EstimatedResponseDTO.builder()
                    .estimatedPriceLower(apiResponse.getRange().getLow())
                    .estimatedPriceHigher(apiResponse.getRange().getHigh())
                    .estimatedDurationMinutes(apiResponse.getDurationMinutes())
                    .build();

        } catch (ResourceAccessException ex) {
            // Không kết nối được API
            return defaultEstimatedResponse();
        } catch (Exception ex) {
            // Các lỗi khác (mapping, null, parse JSON...)
            return defaultEstimatedResponse();
        }
    }

    private EstimatedResponseDTO defaultEstimatedResponse() {
        return EstimatedResponseDTO.builder()
                .estimatedPriceLower(BigDecimal.ZERO)
                .estimatedPriceHigher(BigDecimal.ZERO)
                .estimatedDurationMinutes(0)
                .build();
    }
}
