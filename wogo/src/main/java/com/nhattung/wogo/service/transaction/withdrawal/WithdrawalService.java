package com.nhattung.wogo.service.transaction.withdrawal;

import com.nhattung.wogo.dto.request.*;
import com.nhattung.wogo.dto.response.WithdrawalResponseDTO;
import com.nhattung.wogo.entity.*;
import com.nhattung.wogo.enums.*;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.WithdrawalRepository;
import com.nhattung.wogo.service.notification.INotificationService;
import com.nhattung.wogo.service.payment.IPaymentService;
import com.nhattung.wogo.service.wallet.transaction.IWalletTransactionService;
import com.nhattung.wogo.service.wallet.revenue.IWorkerWalletRevenueService;
import com.nhattung.wogo.service.worker.IWorkerService;
import com.nhattung.wogo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WithdrawalService implements IWithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    private final IWorkerService workerService;
    private final IWalletTransactionService walletTransactionService;
    private final IWorkerWalletRevenueService workerWalletRevenueService;
    private final ModelMapper modelMapper;
    private final INotificationService notificationService;
    private final IPaymentService paymentService;

    @Override
    @Transactional
    public WithdrawalResponseDTO createWithdrawalRequest(WithdrawalRequestDTO request) {

        Worker worker = workerService.getWorkerByUserId(SecurityUtils.getCurrentUserId());

        // 1) Lấy ví doanh thu của thợ
        WorkerWalletRevenue workerWalletRevenue = workerWalletRevenueService.getWalletByUserId();

        // 2) Kiểm tra số dư trước khi tạo yêu cầu rút
        if (workerWalletRevenue.getRevenueBalance().compareTo(request.getAmount()) < 0) {
            throw new AppException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        // 3) Tạo Withdrawal và lưu (vì đã chắc chắn đủ số dư)
        Withdrawal withdrawal = createWithdrawal(request, worker);
        withdrawal = withdrawalRepository.save(withdrawal);

        // 4) Tạo giao dịch ví
        WalletTransaction walletTransaction = walletTransactionService.saveWalletTransaction(
                WalletTransactionRequestDTO.builder()
                        .amount(withdrawal.getAmount())
                        .transactionType(TransactionType.WITHDRAW)
                        .status(PaymentStatus.PENDING)
                        .description("Yêu cầu rút số tiền: " + withdrawal.getAmount() + " VNĐ")
                        .withdrawal(withdrawal)
                        .walletRevenue(workerWalletRevenue)
                        .balanceBefore(workerWalletRevenue.getRevenueBalance())
                        .balanceAfter(workerWalletRevenue.getRevenueBalance().subtract(withdrawal.getAmount()))
                        .build()
        );

        // 5) Tạo Payment
        paymentService.savePayment(
                PaymentRequestDTO.builder()
                        .walletTransaction(walletTransaction)
                        .amount(withdrawal.getAmount())
                        .paymentMethod(PaymentMethod.BANK_TRANSFER)
                        .paymentStatus(PaymentStatus.PENDING)
                        .build()
        );

        // 6) Response
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

        // Cập nhật thông tin rút tiền
        withdrawal.setApproved(approved);
        withdrawal.setPaymentStatus(status);
        withdrawal.setProcessedAt(processedAt);
        withdrawal.setRejectionReason(approved ? null : request.getRejectionReason());

        // Xử lý trạng thái giao dịch ví
        walletTransactionService.processWalletTransaction(
                ProcessWalletTransactionRequestDTO.builder()
                        .transactionId(withdrawal.getWalletTransaction().getId())
                        .status(status)
                        .status(approved ? PaymentStatus.COMPLETED : PaymentStatus.FAILED)
                        .processedAt(processedAt)
                        .build()
        );

        // Trừ ví doanh thu nếu rút thành công
        if (approved) {
            workerWalletRevenueService.updateWalletRevenue(
                    UpdateWalletRequestDTO.builder()
                            .amount(withdrawal.getAmount())
                            .isAdd(false)
                            .build()
            );
        }

        // Soạn nội dung thông báo
        String title = approved ? "Yêu cầu rút tiền đã được chấp thuận" : "Yêu cầu rút tiền bị từ chối";
        String description = approved
                ? String.format("Yêu cầu rút %s VNĐ của bạn đã được xử lý thành công. Số tiền sẽ được chuyển vào tài khoản của bạn.", withdrawal.getAmount())
                : String.format("Yêu cầu rút %s VNĐ của bạn không được chấp thuận. Lý do: %s",
                withdrawal.getAmount(),
                request.getRejectionReason() != null ? request.getRejectionReason() : "Không có lý do cung cấp");

        paymentService.updatePaymentStatus(PaymentRequestDTO.builder()
                .walletTransaction(withdrawal.getWalletTransaction())
                .build());

        // Gửi thông báo
        notificationService.saveNotification(
                NotificationRequestDTO.builder()
                        .targetUserId(withdrawal.getWorker().getUser().getId())
                        .title(title)
                        .description(description)
                        .type(NotificationType.SYSTEM)
                        .targetRole(ROLE.WORKER)
                        .build()
        );

        withdrawalRepository.save(withdrawal);
    }

    @Override
    public List<WithdrawalResponseDTO> getWithdrawalsByApprovalStatus(Boolean isApproved) {
        return withdrawalRepository.findWithdrawalsByApprovalStatus(isApproved)
                .stream()
                .map(this::convertToDTO)
                .toList();

    }


    private WithdrawalResponseDTO convertToDTO(Withdrawal withdrawal) {
        return modelMapper.map(withdrawal, WithdrawalResponseDTO.class);
    }
}
