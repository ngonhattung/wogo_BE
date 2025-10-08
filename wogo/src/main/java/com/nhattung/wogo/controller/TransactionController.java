package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.request.DepositRequestDTO;
import com.nhattung.wogo.dto.request.ProcessDepositRequestDTO;
import com.nhattung.wogo.dto.request.ProcessWithdrawalRequestDTO;
import com.nhattung.wogo.dto.request.WithdrawalRequestDTO;
import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.CreateDepositResponseDTO;
import com.nhattung.wogo.dto.response.WithdrawalResponseDTO;
import com.nhattung.wogo.entity.Deposit;
import com.nhattung.wogo.enums.PaymentStatus;
import com.nhattung.wogo.service.payment.sepay.ISepayVerifyService;
import com.nhattung.wogo.service.transaction.deposit.IDepositService;
import com.nhattung.wogo.service.transaction.withdrawal.IWithdrawalService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final IWithdrawalService withdrawalService;
    private final IDepositService depositService;
    private final ISepayVerifyService sepayVerifyService;
    private final SimpMessagingTemplate messagingTemplate;

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

    @PostMapping("/deposits")
    public ApiResponse<CreateDepositResponseDTO> createDeposit(@RequestBody DepositRequestDTO request) {

        Deposit deposit = depositService.createDepositRequest(request);

        return ApiResponse.<CreateDepositResponseDTO>builder()
                .message("Deposit request created successfully")
                .result(sepayVerifyService.createQRCodeForDeposit(deposit))
                .build();
    }

    //Call mỗi 3s để check trạng thái thanh toán
    @PostMapping("/deposits/verify/{depositId}")
    public ApiResponse<Boolean> verifyDeposit(@PathVariable Long depositId) {
        boolean isDepositSuccess = sepayVerifyService.checkTransactionForDeposit();

        if(isDepositSuccess){

            //Cập nhật trạng thái transaction
            Deposit deposit = depositService.processDeposit(
                    ProcessDepositRequestDTO.builder()
                            .depositId(depositId)
                            .build()
            );
            //Push realtime status cho khách hàng và worker (subscribe theo bookingCode)
            messagingTemplate.convertAndSend(
                    "/topic/depositStatus/" + deposit.getWorker().getUser().getId(), PaymentStatus.COMPLETED
            );
        }

        return ApiResponse.<Boolean>builder()
                .message(isDepositSuccess ? "Payment verified successfully" : "Payment verification failed")
                .result(isDepositSuccess)
                .build();
    }
}
