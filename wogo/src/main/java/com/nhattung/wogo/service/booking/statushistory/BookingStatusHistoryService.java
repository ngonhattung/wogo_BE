package com.nhattung.wogo.service.booking.statushistory;

import com.nhattung.wogo.dto.request.CreateBookingStatusHistoryRequestDTO;
import com.nhattung.wogo.entity.BookingStatusHistory;
import com.nhattung.wogo.repository.BookingStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingStatusHistoryService implements IBookingStatusHistoryService {

    private final BookingStatusHistoryRepository bookingStatusHistoryRepository;

    @Override
    public void saveBookingStatusHistory(CreateBookingStatusHistoryRequestDTO request) {
        bookingStatusHistoryRepository.save(createBookingStatusHistoryEntity(request));
    }

    private BookingStatusHistory createBookingStatusHistoryEntity(CreateBookingStatusHistoryRequestDTO request) {
        return BookingStatusHistory.builder()
                .booking(request.getBooking())
                .oldStatus(request.getOldStatus())
                .newStatus(request.getNewStatus())
                .changedBy(request.getChangedByType())
                .changedByUserId(request.getChangedById())
                .changeReason(request.getReason())
                .build();
    }
}
