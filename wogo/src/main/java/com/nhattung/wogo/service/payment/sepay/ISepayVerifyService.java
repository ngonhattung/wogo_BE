package com.nhattung.wogo.service.payment.sepay;

import com.nhattung.wogo.dto.response.CreateDepositResponseDTO;
import com.nhattung.wogo.entity.Booking;
import com.nhattung.wogo.entity.Deposit;

public interface ISepayVerifyService {
    boolean checkTransactionForPayment(String bookingCode);
    boolean checkTransactionForDeposit();
    String createQRCodeForPayment(Booking booking);
    CreateDepositResponseDTO createQRCodeForDeposit(Deposit deposit);
}
