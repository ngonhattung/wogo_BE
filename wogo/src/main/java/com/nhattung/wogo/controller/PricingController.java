package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.request.PricingSuggestRequestDTO;
import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.PricingSuggestResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pricing")
public class PricingController {

    private final RestTemplate restTemplate;

    @PostMapping("/suggest")
    public ApiResponse<PricingSuggestResponseDTO> suggestPricing(@RequestBody PricingSuggestRequestDTO request){
        var mlUrl = "http://localhost:8000/ml/pricing/suggest";
        PricingSuggestResponseDTO res = restTemplate.postForObject(
                mlUrl,
                request,
                PricingSuggestResponseDTO.class
        );
        if (res == null) {
            return ApiResponse.<PricingSuggestResponseDTO>builder()
                    .code(1001)
                    .message("Failed to get pricing suggestion from ML service")
                    .build();
        }
        return ApiResponse.<PricingSuggestResponseDTO>builder()
                .code(1000)
                .message("Pricing suggestion retrieved successfully")
                .result(res)
                .build();
    }
}
