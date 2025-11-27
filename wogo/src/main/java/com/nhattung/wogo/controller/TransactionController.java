package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.request.DepositRequestDTO;
import com.nhattung.wogo.dto.request.ProcessDepositRequestDTO;
import com.nhattung.wogo.dto.request.ProcessWithdrawalRequestDTO;
import com.nhattung.wogo.dto.request.WithdrawalRequestDTO;
import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.CreateDepositResponseDTO;
import com.nhattung.wogo.dto.response.WalletTransactionResponseDTO;
import com.nhattung.wogo.dto.response.WithdrawalResponseDTO;
import com.nhattung.wogo.entity.Deposit;
import com.nhattung.wogo.enums.PaymentStatus;
import com.nhattung.wogo.service.payment.sepay.ISepayVerifyService;
import com.nhattung.wogo.service.transaction.deposit.IDepositService;
import com.nhattung.wogo.service.transaction.withdrawal.IWithdrawalService;
import com.nhattung.wogo.service.wallet.expense.IWorkerWalletExpenseService;
import com.nhattung.wogo.service.wallet.revenue.IWorkerWalletRevenueService;
import com.nhattung.wogo.service.wallet.transaction.IWalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final IWithdrawalService withdrawalService;
    private final IWorkerWalletRevenueService walletRevenueService;
    private final IWorkerWalletExpenseService walletExpenseService;
    private final IDepositService depositService;
    private final ISepayVerifyService sepayVerifyService;
    private final SimpMessagingTemplate messagingTemplate;
    private final IWalletTransactionService walletTransactionService;


    @GetMapping("/walletRevenueBalance")
    public ApiResponse<BigDecimal> getWalletRevenueBalance() {
        return ApiResponse.<BigDecimal>builder()
                .message("Wallet revenue balance retrieved successfully")
                .result(walletRevenueService.getWalletByUserId().getRevenueBalance())
                .build();
    }

    @GetMapping("/walletExpenseBalance")
    public ApiResponse<BigDecimal> getWalletExpenseBalance() {
        return ApiResponse.<BigDecimal>builder()
                .message("Wallet expense balance retrieved successfully")
                .result(walletExpenseService.getWalletByUserId().getExpenseBalance())
                .build();
    }

    @GetMapping("/withdrawals/history/{workerId}")
    public ApiResponse<List<WalletTransactionResponseDTO>> getHistoryWithdrawals(@PathVariable Long workerId) {
        return ApiResponse.<List<WalletTransactionResponseDTO>>builder()
                .message("Withdrawal history retrieved successfully")
                .result(walletTransactionService.getHistoryWithdrawalTransactions(workerId))
                .build();
    }

    @GetMapping("/deposits/history/{workerId}")
    public ApiResponse<List<WalletTransactionResponseDTO>> getHistoryDeposits(@PathVariable Long workerId) {
        return ApiResponse.<List<WalletTransactionResponseDTO>>builder()
                .message("Deposit history retrieved successfully")
                .result(walletTransactionService.getHistoryDepositTransactions(workerId))
                .build();
    }


    @GetMapping("/withdrawals/getWithdrawalsByStatus")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<List<WithdrawalResponseDTO>> getWithdrawalsByStatus(
            @RequestParam(required = false) Boolean isApproved
    ) {
        return ApiResponse.<List<WithdrawalResponseDTO>>builder()
                .message("Withdrawals retrieved successfully")
                .result(withdrawalService.getWithdrawalsByApprovalStatus(isApproved))
                .build();
    }

    @PostMapping("/withdrawals")
    public ApiResponse<WithdrawalResponseDTO> createWithdrawal(@RequestBody WithdrawalRequestDTO request) {
        return ApiResponse.<WithdrawalResponseDTO>builder()
                .message("Withdrawal request created successfully")
                .result(withdrawalService.createWithdrawalRequest(request))
                .build();

    }
    @PutMapping("/withdrawals/process")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
        boolean isDepositSuccess = sepayVerifyService.checkTransactionForDeposit(depositId);

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
