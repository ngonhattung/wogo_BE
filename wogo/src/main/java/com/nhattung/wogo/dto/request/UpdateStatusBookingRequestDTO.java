package com.nhattung.wogo.dto.request;

import com.nhattung.wogo.enums.BookingStatus;
import lombok.Data;

@Data
public class UpdateStatusBookingRequestDTO {

    private String bookingCode;
    private BookingStatus status;

}
