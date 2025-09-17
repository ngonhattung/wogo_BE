package com.nhattung.wogo.service.payment;

import com.nhattung.wogo.dto.request.PaymentRequestDTO;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.PaymentResponseDTO;
import com.nhattung.wogo.entity.Booking;
import com.nhattung.wogo.entity.BookingPayment;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.enums.PaymentMethod;
import com.nhattung.wogo.enums.PaymentStatus;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.BookingRepository;
import com.nhattung.wogo.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService  implements IPaymentService{

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public void savePayment(PaymentRequestDTO request) {

        Booking booking = bookingRepository.findByBookingCode(request.getBookingCode())
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));


        paymentRepository.save(BookingPayment.builder()
                        .booking(booking)
                        .totalAmount(request.getAmount())
                        .paymentMethod(request.getPaymentMethod())
                        .paymentStatus(PaymentStatus.PENDING)
                        .paymentDate(LocalDateTime.now())
                        .paidAt(request.getPaidAt())
                .build());

    }


    @Override
    public PageResponse<PaymentResponseDTO> getAllPayments(int page, int size) {
        return null;
    }



}
