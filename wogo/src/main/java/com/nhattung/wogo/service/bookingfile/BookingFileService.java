package com.nhattung.wogo.service.bookingfile;

import com.nhattung.wogo.dto.response.BookingFileDTO;
import com.nhattung.wogo.dto.response.UploadS3Response;
import com.nhattung.wogo.entity.Booking;
import com.nhattung.wogo.entity.BookingFile;
import com.nhattung.wogo.repository.BookingFileRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookingFileService implements IBookingFileService {

    private final BookingFileRepository bookingFileRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public void saveFiles(List<UploadS3Response> files, Booking booking) {
        if (files == null || files.isEmpty()) {
            return; // hoặc throw exception nếu muốn ép phải có file
        }

        List<BookingFile> bookingFiles = files.stream()
                .filter(Objects::nonNull)
                .map(file -> BookingFile.builder()
                        .fileName(file.getFileName())
                        .fileType(file.getFileType())
                        .fileUrl(file.getFileUrl())
                        .booking(booking)
                        .build())
                .toList();

        if (!bookingFiles.isEmpty()) {
            bookingFileRepository.saveAll(bookingFiles);
        }
    }

    @Override
    public List<BookingFileDTO> getFilesByBookingId(Long bookingId) {
        return bookingFileRepository.findByBookingId(bookingId)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    private BookingFileDTO convertToDto(BookingFile bookingFile) {
        return modelMapper.map(bookingFile, BookingFileDTO.class);
    }


}
