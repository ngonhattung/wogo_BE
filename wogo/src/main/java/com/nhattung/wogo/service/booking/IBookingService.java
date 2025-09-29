package com.nhattung.wogo.service.booking;

import com.nhattung.wogo.dto.request.*;
import com.nhattung.wogo.dto.response.*;
import com.nhattung.wogo.entity.Booking;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IBookingService {

    JobResponseDTO createJob(FindServiceRequestDTO request, List<MultipartFile> files);
    List<JobSummaryResponseDTO> getListPendingJobsMatchWorker();
    Booking saveBooking(BookingRequestDTO request);
    boolean verifyJobRequest(SendQuoteRequestDTO request);
    WorkerQuoteResponseDTO sendQuote(SendQuoteRequestDTO request);
    BookingResponseDTO placeJob(PlaceJobRequestDTO request);
    void saveLocation(String bookingCode,RealtimeLocationDTO request);
    RealtimeLocationDTO getLocation(String bookingCode);
    void updateStatusBooking(UpdateStatusBookingRequestDTO request);
    double haversine(HaversineRequestDTO request);
    TransactionResponseDTO createBookingTransaction(String bookingCode);
    BookingResponseDTO confirmPrice(ConfirmPriceRequestDTO request);
    List<BookingHistoryResponseDTO> getBookingHistory();
}
