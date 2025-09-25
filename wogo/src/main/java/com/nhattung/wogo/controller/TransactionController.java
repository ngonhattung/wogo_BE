package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.request.ProcessWithdrawalRequestDTO;
import com.nhattung.wogo.dto.request.WithdrawalRequestDTO;
import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.WithdrawalResponseDTO;
import com.nhattung.wogo.service.transaction.IWithdrawalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
public class TransactionController {
    private final IWithdrawalService withdrawalService;

    @PostMapping("/withdrawals")
    public ApiResponse<WithdrawalResponseDTO> createWithdrawal(@RequestBody WithdrawalRequestDTO request) {
        return ApiResponse.<WithdrawalResponseDTO>builder()
                .message("Withdrawal request created successfully")
                .result(withdrawalService.createWithdrawalRequest(request))
                .build();

    }
    @PutMapping("/withdrawals/process")
    public ApiResponse<Void> processWithdrawal(@RequestBody ProcessWithdrawalRequestDTO request) {
        withdrawalService.processWithdrawal(request);
        return ApiResponse.<Void>builder()
                .message("Withdrawal request processed successfully")
                .build();
    }
}
