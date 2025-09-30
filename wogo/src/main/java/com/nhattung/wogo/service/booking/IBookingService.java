package com.nhattung.wogo.service.booking;

import com.nhattung.wogo.dto.request.*;
import com.nhattung.wogo.dto.response.*;
import com.nhattung.wogo.entity.Booking;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IBookingService {

    JobResponseDTO createJob(FindServiceRequestDTO request, List<MultipartFile> files);

    Booking saveBooking(BookingRequestDTO request);
    TransactionResponseDTO createBookingTransaction(String bookingCode);
    void saveLocation(String bookingCode,RealtimeLocationDTO request);
    boolean verifyJobRequest(SendQuoteRequestDTO request);
    void updateStatusBooking(UpdateStatusBookingRequestDTO request);
    double haversine(HaversineRequestDTO request);

    WorkerQuoteResponseDTO sendQuote(SendQuoteRequestDTO request);
    BookingResponseDTO confirmPrice(ConfirmPriceRequestDTO request);
    BookingResponseDTO placeJob(PlaceJobRequestDTO request);

    RealtimeLocationDTO getLocation(String bookingCode);
    List<BookingHistoryResponseDTO> getBookingHistory();
    List<JobSummaryResponseDTO> getListPendingJobsMatchWorker();
    Booking getBookingByCode(String bookingCode);
}
