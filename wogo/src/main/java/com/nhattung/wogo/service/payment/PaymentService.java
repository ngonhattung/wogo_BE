package com.nhattung.wogo.service.payment;

import com.nhattung.wogo.dto.request.PaymentRequestDTO;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.PaymentResponseDTO;
import com.nhattung.wogo.entity.Payment;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.enums.PaymentMethod;
import com.nhattung.wogo.enums.PaymentStatus;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService  implements IPaymentService{

    private final PaymentRepository paymentRepository;

    @Override
    public void savePayment(PaymentRequestDTO request) {
        Payment.PaymentBuilder paymentBuilder = Payment.builder()
                .totalAmount(request.getAmount())
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(request.getPaymentMethod());

        if (request.getBooking() != null) {
            paymentBuilder.booking(request.getBooking());
        }
        else if (request.getWalletTransaction() != null) {
            paymentBuilder.walletTransaction(request.getWalletTransaction());
        }
        else {
            throw new AppException(ErrorCode.INVALID_PAYMENT);
        }

        paymentRepository.save(paymentBuilder.build());
    }

    @Override
    public PageResponse<PaymentResponseDTO> getAllPayments(int page, int size) {
        return null;
    }

    @Override
    public Payment updatePaymentStatus(PaymentRequestDTO request) {

        boolean isBookingPayment = request.getBooking() != null;
        boolean isWalletPayment = request.getWalletTransaction() != null;

        // Không hợp lệ nếu cả hai đều có hoặc cả hai đều không có
        if (isBookingPayment == isWalletPayment) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_TYPE);
        }

        Payment payment;

        // ===== TRƯỜNG HỢP 1: THANH TOÁN BOOKING =====
        if (isBookingPayment) {
            payment = paymentRepository.findByBooking(request.getBooking())
                    .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

            validatePaymentMethod(request.getPaymentMethod());

            payment.setPaymentMethod(request.getPaymentMethod());
            payment.setPaymentStatus(PaymentStatus.COMPLETED); // ví dụ booking đã thanh toán OK
        }

        // ===== TRƯỜNG HỢP 2: THANH TOÁN VÍ =====
        else {
            payment = paymentRepository.findByWalletTransaction(request.getWalletTransaction())
                    .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

            payment.setPaymentStatus(request.getWalletTransaction().getPaymentStatus());
            // phương thức thanh toán không cần cập nhật trong giao dịch ví
        }

        return paymentRepository.save(payment);
    }

    private void validatePaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_METHOD);
        }
    }

    // Kiểm tra tính hợp lệ của phương thức thanh toán
    private boolean isValidPaymentMethod(PaymentMethod paymentMethod) {
        return paymentMethod == PaymentMethod.CASH || paymentMethod == PaymentMethod.BANK_TRANSFER;
    }

    // Cập nhật trạng thái thanh toán
    private Payment updatePayment(Payment payment) {
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }



}
