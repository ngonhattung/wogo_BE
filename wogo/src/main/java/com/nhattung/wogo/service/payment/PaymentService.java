package com.nhattung.wogo.service.payment;

import com.nhattung.wogo.dto.request.PaymentRequestDTO;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.PaymentResponseDTO;
import com.nhattung.wogo.entity.BookingPayment;
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
        paymentRepository.save(BookingPayment.builder()
                        .booking(request.getBooking())
                        .totalAmount(request.getAmount())
                        .paymentStatus(PaymentStatus.PENDING)
                .build());
    }

    @Override
    public PageResponse<PaymentResponseDTO> getAllPayments(int page, int size) {
        return null;
    }

    @Override
    public BookingPayment updatePaymentStatus(PaymentRequestDTO request) {
        BookingPayment payment = paymentRepository.findByBooking(request.getBooking())
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        // Xử lý cập nhật trạng thái thanh toán
        if (isValidPaymentMethod(request.getPaymentMethod())) {
            return updatePayment(payment);
        } else {
            throw new AppException(ErrorCode.INVALID_PAYMENT_STATUS);
        }
    }

    // Kiểm tra tính hợp lệ của phương thức thanh toán
    private boolean isValidPaymentMethod(PaymentMethod paymentMethod) {
        return paymentMethod == PaymentMethod.CASH || paymentMethod == PaymentMethod.BANK_TRANSFER;
    }

    // Cập nhật trạng thái thanh toán
    private BookingPayment updatePayment(BookingPayment payment) {
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }



}
