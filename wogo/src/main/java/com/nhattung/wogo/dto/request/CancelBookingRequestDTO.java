package com.nhattung.wogo.dto.request;

import com.nhattung.wogo.enums.ActorType;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class CancelBookingRequestDTO {
    private String bookingCode;
    private ActorType canceller;
    private String reason;
}
