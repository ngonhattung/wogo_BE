package com.nhattung.wogo.service.booking;

import com.nhattung.wogo.dto.request.BookingRequestDTO;
import com.nhattung.wogo.dto.request.FindServiceRequestDTO;
import com.nhattung.wogo.dto.response.JobRequestResponseDTO;
import com.nhattung.wogo.entity.Booking;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IBookingService {

    void findWorkers(FindServiceRequestDTO request, List<MultipartFile> files);
    List<JobRequestResponseDTO> getJobRequestsByListServiceId(List<Long> serviceIds);
    Booking saveBooking(BookingRequestDTO request);
}
