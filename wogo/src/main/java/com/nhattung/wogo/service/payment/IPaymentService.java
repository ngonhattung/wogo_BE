package com.nhattung.wogo.service.payment;

import com.nhattung.wogo.dto.request.PaymentRequestDTO;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.PaymentResponseDTO;
import com.nhattung.wogo.entity.Payment;

public interface IPaymentService {

    void savePayment(PaymentRequestDTO request);
    PageResponse<PaymentResponseDTO> getAllPayments(int page, int size);
    Payment updatePaymentStatus(PaymentRequestDTO request);

}
