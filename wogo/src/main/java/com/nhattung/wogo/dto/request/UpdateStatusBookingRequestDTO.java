package com.nhattung.wogo.dto.request;

import com.nhattung.wogo.enums.BookingStatus;
import com.nhattung.wogo.enums.PaymentMethod;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateStatusBookingRequestDTO {

    private String bookingCode;
    private BookingStatus status;
    private PaymentMethod paymentMethod;

}
