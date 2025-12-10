package com.nhattung.wogo.service.payment;

import com.nhattung.wogo.dto.request.PaymentRequestDTO;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.PaymentResponseDTO;
import com.nhattung.wogo.entity.Booking;
import com.nhattung.wogo.entity.Payment;
import com.nhattung.wogo.entity.WalletTransaction;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.enums.PaymentMethod;
import com.nhattung.wogo.enums.PaymentStatus;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.BookingRepository;
import com.nhattung.wogo.repository.PaymentRepository;
import com.nhattung.wogo.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService  implements IPaymentService{

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    @Override
    public void savePayment(PaymentRequestDTO request) {

        Booking booking = null;
        WalletTransaction walletTransaction = null;

        // ===== TRƯỜNG HỢP 1: Có bookingId =====
        if (request.getBookingId() != null) {
            booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

            // Check: booking này đã có payment chưa (OneToOne → chỉ được 1)
            if (paymentRepository.existsByBooking(booking)) {
                throw new AppException(ErrorCode.PAYMENT_ALREADY_EXISTS);
            }
        }

        // ===== TRƯỜNG HỢP 2: Có walletTransactionId =====
        if (request.getWalletTransactionId() != null) {
            walletTransaction = walletTransactionRepository.findById(request.getWalletTransactionId())
                    .orElseThrow(() -> new AppException(ErrorCode.WALLET_TRANSACTION_NOT_FOUND));

            // Check: walletTransaction này đã có payment chưa
            if (paymentRepository.existsByWalletTransaction(walletTransaction)) {
                throw new AppException(ErrorCode.PAYMENT_ALREADY_EXISTS);
            }
        }

        // ===== TRƯỜNG HỢP KHÔNG HỢP LỆ =====
        if (booking == null && walletTransaction == null) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_TYPE);
        }

        // ===== BUILD PAYMENT =====
        Payment payment = Payment.builder()
                .totalAmount(request.getAmount())
                .paymentStatus(request.getPaymentStatus())
                .paymentMethod(request.getPaymentMethod())
                .booking(booking)
                .walletTransaction(walletTransaction)
                .build();

        paymentRepository.save(payment);
    }


    @Override
    public PageResponse<PaymentResponseDTO> getAllPayments(int page, int size) {
        return null;
    }

    @Override
    public Payment updatePaymentStatus(PaymentRequestDTO request) {

        boolean isBookingPayment = request.getBookingId() != null;
        boolean isWalletPayment = request.getWalletTransactionId() != null;

        // Không cho gửi cả hai hoặc không gửi cái nào
        if (isBookingPayment == isWalletPayment) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_TYPE);
        }

        Payment payment;

        // ===================== THANH TOÁN BOOKING =====================
        if (isBookingPayment) {

            // Load booking từ DB
            Booking booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

            // Tìm Payment theo booking (entity real)
            payment = paymentRepository.findByBooking(booking)
                    .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

            // Validate payment method
            validatePaymentMethod(request.getPaymentMethod());

            // Update payment
            payment.setPaymentMethod(request.getPaymentMethod());
            payment.setPaymentStatus(PaymentStatus.COMPLETED);  // ví dụ hoàn tất
            payment.setPaidAt(LocalDateTime.now());
        }

        // ===================== THANH TOÁN VÍ =====================
        else {

            // Load walletTransaction từ DB
            WalletTransaction wallet = walletTransactionRepository.findById(request.getWalletTransactionId())
                    .orElseThrow(() -> new AppException(ErrorCode.WALLET_TRANSACTION_NOT_FOUND));

            // Tìm Payment theo wallet transaction
            payment = paymentRepository.findByWalletTransaction(wallet)
                    .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

            // Update trạng thái
            payment.setPaymentStatus(request.getPaymentStatus());
            payment.setPaidAt(LocalDateTime.now());
        }

        // Lúc này payment chắc chắn là managed entity → save() chỉ UPDATE, không INSERT
        return paymentRepository.save(payment);
    }


    private void validatePaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_METHOD);
        }
    }


}
