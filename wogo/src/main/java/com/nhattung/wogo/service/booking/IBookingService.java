package com.nhattung.wogo.service.booking;

import com.nhattung.wogo.dto.request.*;
import com.nhattung.wogo.dto.response.BookingResponseDTO;
import com.nhattung.wogo.dto.response.JobRequestResponseDTO;
import com.nhattung.wogo.dto.response.WorkerFoundResponseDTO;
import com.nhattung.wogo.entity.Booking;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IBookingService {

    JobRequestResponseDTO createJob(FindServiceRequestDTO request, List<MultipartFile> files);
    List<JobRequestResponseDTO> getListPendingJobsMatchWorker();
    Booking saveBooking(BookingRequestDTO request);
    boolean verifyJobRequest(JobRequestDTO request);
    WorkerFoundResponseDTO sendQuote(JobRequestDTO request);
    JobRequestResponseDTO getJobByCode(String jobRequestCode);
    BookingResponseDTO placeJob(PlaceJobRequestDTO request);
    void saveLocation(String bookingCode,RealtimeLocationDTO request);
    RealtimeLocationDTO getLocation(String bookingCode);
    void updateStatusBooking(UpdateStatusBookingRequestDTO request);
    double haversine(HaversineRequestDTO request);

}
