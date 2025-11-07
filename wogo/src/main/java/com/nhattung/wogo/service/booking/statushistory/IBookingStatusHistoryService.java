package com.nhattung.wogo.service.booking.statushistory;

import com.nhattung.wogo.dto.request.CreateBookingStatusHistoryRequestDTO;

public interface IBookingStatusHistoryService {

    void saveBookingStatusHistory(CreateBookingStatusHistoryRequestDTO request);
}
