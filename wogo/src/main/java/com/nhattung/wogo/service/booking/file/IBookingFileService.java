package com.nhattung.wogo.service.booking.file;

import com.nhattung.wogo.dto.response.BookingFileDTO;
import com.nhattung.wogo.dto.response.JobFileResponseDTO;
import com.nhattung.wogo.entity.Booking;

import java.util.List;

public interface IBookingFileService {

    void saveFiles(List<JobFileResponseDTO> files, Booking booking);
    List<BookingFileDTO> getFilesByBookingId(Long bookingId);

}
