package com.nhattung.wogo.service.bookingfile;

import com.nhattung.wogo.dto.response.BookingFileDTO;
import com.nhattung.wogo.dto.response.UploadS3Response;
import com.nhattung.wogo.entity.Booking;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IBookingFileService {

    void saveFiles(List<UploadS3Response> files, Booking booking);
    List<BookingFileDTO> getFilesByBookingId(Long bookingId);

}
