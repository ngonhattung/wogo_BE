package com.nhattung.wogo.dto.request;

import com.nhattung.wogo.entity.Booking;
import com.nhattung.wogo.enums.ActorType;
import com.nhattung.wogo.enums.BookingStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateBookingStatusHistoryRequestDTO {
    private Booking booking;
    private BookingStatus oldStatus;
    private BookingStatus newStatus;
    private Long changedById;
    private ActorType changedByType;
    private String reason;
}
