package com.nhattung.wogo.service.transaction.deposit;

import com.nhattung.wogo.dto.request.*;
import com.nhattung.wogo.entity.Deposit;
import com.nhattung.wogo.entity.WalletTransaction;
import com.nhattung.wogo.entity.Worker;
import com.nhattung.wogo.entity.WorkerWalletExpense;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.enums.PaymentMethod;
import com.nhattung.wogo.enums.PaymentStatus;
import com.nhattung.wogo.enums.TransactionType;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.DepositRepository;
import com.nhattung.wogo.service.payment.IPaymentService;
import com.nhattung.wogo.service.wallet.expense.IWorkerWalletExpenseService;
import com.nhattung.wogo.service.wallet.transaction.IWalletTransactionService;
import com.nhattung.wogo.service.worker.IWorkerService;
import com.nhattung.wogo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DepositService implements IDepositService {

    private final DepositRepository depositRepository;
    private final IWorkerService workerService;
    private final IWorkerWalletExpenseService workerWalletExpenseService;
    private final IWalletTransactionService walletTransactionService;
    private final IPaymentService paymentService;

    @Override
    @Transactional
    public Deposit createDepositRequest(DepositRequestDTO request) {

        Worker worker = workerService.getWorkerByUserId(SecurityUtils.getCurrentUserId());

        // 1) Tạo và lưu Deposit trước
        Deposit deposit = createDeposit(request, worker);
        deposit = depositRepository.save(deposit);

        // 2) Lấy ví chi tiêu của thợ
        WorkerWalletExpense walletExpense = workerWalletExpenseService.getWalletByUserId();

        // 3) Lưu giao dịch ví (gán deposit đã lưu -> không lỗi TransientObjectException)
        WalletTransaction walletTransaction = walletTransactionService.saveWalletTransaction(
                WalletTransactionRequestDTO.builder()
                        .amount(deposit.getAmount())
                        .transactionType(TransactionType.DEPOSIT)
                        .status(PaymentStatus.PENDING)
                        .description("Yêu cầu nạp số tiền: " + deposit.getAmount() + " VNĐ")
                        .deposit(deposit)
                        .walletExpense(walletExpense)
                        .balanceBefore(walletExpense.getExpenseBalance())
                        .balanceAfter(walletExpense.getExpenseBalance().add(deposit.getAmount()))
                        .build()
        );

        // 4) Tạo Payment từ giao dịch ví
        paymentService.savePayment(
                PaymentRequestDTO.builder()
                        .walletTransaction(walletTransaction)
                        .amount(deposit.getAmount())
                        .paymentMethod(PaymentMethod.BANK_TRANSFER)
                        .build()
        );

        return deposit;
    }

    @Override
    public Deposit processDeposit(ProcessDepositRequestDTO request) {

        Deposit deposit = depositRepository.findById(request.getDepositId())
                .orElseThrow(() -> new AppException(ErrorCode.DEPOSIT_NOT_FOUND));

        deposit.setProcessedAt(LocalDateTime.now());
        deposit.setPaymentStatus(PaymentStatus.COMPLETED);

        walletTransactionService.processWalletTransaction(
                ProcessWalletTransactionRequestDTO.builder()
                        .transactionId(deposit.getWalletTransaction().getId())
                        .status(PaymentStatus.COMPLETED)
                        .processedAt(LocalDateTime.now())
                        .build()
        );

        //Cộng tiền vào ví chi phí
        workerWalletExpenseService.updateWalletExpense(UpdateWalletRequestDTO.builder()
                .amount(deposit.getAmount())
                .isAdd(true)
                .build());

        paymentService.updatePaymentStatus(PaymentRequestDTO.builder()
                .walletTransaction(deposit.getWalletTransaction())
                .build());

        return depositRepository.save(deposit);
    }

    private Deposit createDeposit(DepositRequestDTO request, Worker worker) {
        Deposit deposit = Deposit.builder()
                .amount(request.getAmount())
                .worker(worker)
                .paymentStatus(PaymentStatus.PENDING)
                .depositMethod(PaymentMethod.BANK_TRANSFER)
                .requestedAt(LocalDateTime.now())
                .build();
        return depositRepository.save(deposit);
    }
}
