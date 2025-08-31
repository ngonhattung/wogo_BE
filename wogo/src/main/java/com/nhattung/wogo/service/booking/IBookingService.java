package com.nhattung.wogo.service.booking;

import com.nhattung.wogo.dto.request.JobRequestDTO;
import com.nhattung.wogo.dto.request.BookingRequestDTO;
import com.nhattung.wogo.dto.request.FindServiceRequestDTO;
import com.nhattung.wogo.dto.request.PlaceJobRequestDTO;
import com.nhattung.wogo.dto.response.BookingResponseDTO;
import com.nhattung.wogo.dto.response.JobRequestResponseDTO;
import com.nhattung.wogo.dto.response.WorkerFoundResponseDTO;
import com.nhattung.wogo.entity.Booking;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IBookingService {

    JobRequestResponseDTO createJob(FindServiceRequestDTO request, List<MultipartFile> files);
    List<JobRequestResponseDTO> listPendingJobsByServiceIds(List<Long> serviceIds);
    Booking saveBooking(BookingRequestDTO request);
    boolean verifyJobRequest(JobRequestDTO request);
    WorkerFoundResponseDTO sendQuote(JobRequestDTO request);
    JobRequestResponseDTO getJobByCode(String jobRequestCode);
    BookingResponseDTO placeJob(PlaceJobRequestDTO request);
}
