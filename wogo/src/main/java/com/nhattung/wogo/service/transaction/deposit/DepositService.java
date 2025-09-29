package com.nhattung.wogo.service.transaction.deposit;

import com.nhattung.wogo.dto.request.*;
import com.nhattung.wogo.entity.Deposit;
import com.nhattung.wogo.entity.Worker;
import com.nhattung.wogo.entity.WorkerWalletExpense;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.enums.PaymentMethod;
import com.nhattung.wogo.enums.PaymentStatus;
import com.nhattung.wogo.enums.TransactionType;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.DepositRepository;
import com.nhattung.wogo.service.wallet.expense.IWorkerWalletExpenseService;
import com.nhattung.wogo.service.wallet.transaction.IWalletTransactionService;
import com.nhattung.wogo.service.worker.IWorkerService;
import com.nhattung.wogo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DepositService implements IDepositService {

    private final DepositRepository depositRepository;
    private final IWorkerService workerService;
    private final IWorkerWalletExpenseService workerWalletExpenseService;
    private final IWalletTransactionService walletTransactionService;

    @Override
    public Deposit createDepositRequest(DepositRequestDTO request) {

        Worker worker = workerService.getWorkerByUserId(SecurityUtils.getCurrentUserId());
        Deposit deposit = createDeposit(request, worker);
        WorkerWalletExpense walletExpense = workerWalletExpenseService.getWalletByUserId();

        walletTransactionService.saveWalletTransaction(
                WalletTransactionRequestDTO.builder()
                        .amount(deposit.getAmount())
                        .transactionType(TransactionType.DEPOSIT)
                        .status(PaymentStatus.PENDING)
                        .description("Withdrawal request for amount: " + deposit.getAmount())
                        .deposit(deposit)
                        .walletExpense(walletExpense)
                        .balanceBefore(walletExpense.getExpenseBalance())
                        .balanceAfter(walletExpense.getExpenseBalance().add(deposit.getAmount()))
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
