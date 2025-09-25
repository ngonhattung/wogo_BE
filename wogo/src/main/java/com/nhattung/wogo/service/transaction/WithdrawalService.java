package com.nhattung.wogo.service.transaction;

import com.nhattung.wogo.dto.request.ProcessWalletTransactionRequestDTO;
import com.nhattung.wogo.dto.request.ProcessWithdrawalRequestDTO;
import com.nhattung.wogo.dto.request.WalletTransactionRequestDTO;
import com.nhattung.wogo.dto.request.WithdrawalRequestDTO;
import com.nhattung.wogo.dto.response.WithdrawalResponseDTO;
import com.nhattung.wogo.dto.response.WorkerWalletRevenueResponseDTO;
import com.nhattung.wogo.entity.WalletTransaction;
import com.nhattung.wogo.entity.Withdrawal;
import com.nhattung.wogo.entity.Worker;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.enums.PaymentMethod;
import com.nhattung.wogo.enums.PaymentStatus;
import com.nhattung.wogo.enums.TransactionType;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.WithdrawalRepository;
import com.nhattung.wogo.service.wallet.transaction.IWalletTransactionService;
import com.nhattung.wogo.service.wallet.revenue.IWorkerWalletRevenueService;
import com.nhattung.wogo.service.worker.IWorkerService;
import com.nhattung.wogo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WithdrawalService implements IWithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    private final IWorkerService workerService;
    private final IWalletTransactionService walletTransactionService;
    private final IWorkerWalletRevenueService workerWalletRevenueService;
    private final ModelMapper modelMapper;

    @Override
    public WithdrawalResponseDTO createWithdrawalRequest(WithdrawalRequestDTO request) {

        Worker worker = workerService.getWorkerByUserId(SecurityUtils.getCurrentUserId());
        Withdrawal withdrawal = createWithdrawal(request, worker);
        WorkerWalletRevenueResponseDTO workerWalletRevenue = workerWalletRevenueService.getWalletByUserId();

        WalletTransaction walletTransaction = walletTransactionService.saveWalletTransaction(
                WalletTransactionRequestDTO.builder()
                        .amount(withdrawal.getAmount())
                        .transactionType(TransactionType.WITHDRAW)
                        .status(com.nhattung.wogo.enums.PaymentStatus.PENDING)
                        .description("Withdrawal request for amount: " + withdrawal.getAmount())
                        .withdrawal(withdrawal)
                        .balanceBefore(workerWalletRevenue.getRevenueBalance())
                        .balanceAfter(workerWalletRevenue.getRevenueBalance().subtract(withdrawal.getAmount()))
                        .build()
        );

        WithdrawalResponseDTO responseDTO = convertToDTO(withdrawal);
        responseDTO.setTransactionCode(walletTransaction.getTransactionCode());
        responseDTO.setTransactionType(walletTransaction.getTransactionType());

        return responseDTO;
    }

    private Withdrawal createWithdrawal(WithdrawalRequestDTO request, Worker worker) {
        return Withdrawal.builder()
                .bankAccountNumber(request.getBankAccountNumber())
                .bankName(request.getBankName())
                .amount(request.getAmount())
                .taxAmount(BigDecimal.ZERO) // Giả sử không có thuế ban đầu
                .netAmount(request.getAmount()) // Giả sử không có thuế ban đầu
                .paymentStatus(PaymentStatus.PENDING) // Trạng thái ban đầu
                .withdrawalMethod(PaymentMethod.BANK_TRANSFER)
                .requestedAt(LocalDateTime.now())
                .approved(false)
                .processedAt(null)
                .rejectionReason(null)
                .worker(worker)
                .build();
    }

    @Override
    public void processWithdrawal(ProcessWithdrawalRequestDTO request) {
        Withdrawal withdrawal = withdrawalRepository.findById(request.getWithdrawalId())
                .orElseThrow(() -> new AppException(ErrorCode.WITHDRAWAL_NOT_FOUND));

        boolean approved = request.isApproved();
        LocalDateTime processedAt = LocalDateTime.now();
        PaymentStatus status = approved ? PaymentStatus.COMPLETED : PaymentStatus.FAILED;

        withdrawal.setApproved(approved);
        withdrawal.setPaymentStatus(status);
        withdrawal.setProcessedAt(processedAt);
        withdrawal.setRejectionReason(approved ? null : request.getRejectionReason());

        walletTransactionService.processWalletTransaction(
                ProcessWalletTransactionRequestDTO.builder()
                        .transactionId(withdrawal.getWalletTransaction().getId())
                        .status(status)
                        .processedAt(processedAt)
                        .build()
        );

        withdrawalRepository.save(withdrawal);
    }

    private WithdrawalResponseDTO convertToDTO(Withdrawal withdrawal) {
        return modelMapper.map(withdrawal, WithdrawalResponseDTO.class);
    }
}
