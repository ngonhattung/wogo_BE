package com.nhattung.wogo.service.booking;

import com.nhattung.wogo.dto.request.AcceptJobRequestDTO;
import com.nhattung.wogo.dto.request.BookingRequestDTO;
import com.nhattung.wogo.dto.request.FindServiceRequestDTO;
import com.nhattung.wogo.dto.response.JobRequestResponseDTO;
import com.nhattung.wogo.dto.response.WorkerFoundResponseDTO;
import com.nhattung.wogo.entity.Booking;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IBookingService {

    JobRequestResponseDTO createJob(FindServiceRequestDTO request, List<MultipartFile> files);
    List<JobRequestResponseDTO> listPendingJobsByServiceIds(List<Long> serviceIds);
    void saveBooking(BookingRequestDTO request);
    boolean verifyJobRequest(AcceptJobRequestDTO request);
    WorkerFoundResponseDTO acceptJobRequest(AcceptJobRequestDTO request);
    JobRequestResponseDTO getJobByCode(String jobRequestCode);
}
