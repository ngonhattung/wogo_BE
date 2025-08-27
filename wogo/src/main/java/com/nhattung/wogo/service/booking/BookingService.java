package com.nhattung.wogo.service.booking;

import com.nhattung.wogo.dto.request.BookingRequestDTO;
import com.nhattung.wogo.dto.request.FindServiceRequestDTO;
import com.nhattung.wogo.dto.response.JobRequestResponseDTO;
import com.nhattung.wogo.dto.response.UploadS3Response;
import com.nhattung.wogo.dto.response.UserResponseDTO;
import com.nhattung.wogo.dto.response.WorkerFoundResponseDTO;
import com.nhattung.wogo.entity.Booking;
import com.nhattung.wogo.entity.ServiceWG;
import com.nhattung.wogo.entity.User;
import com.nhattung.wogo.entity.WorkerService;
import com.nhattung.wogo.enums.BookingStatus;
import com.nhattung.wogo.repository.BookingRepository;
import com.nhattung.wogo.service.service.IServiceService;
import com.nhattung.wogo.service.user.UserService;
import com.nhattung.wogo.utils.SecurityUtils;
import com.nhattung.wogo.utils.UploadToS3;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final IServiceService serviceService;
    private final UploadToS3 uploadToS3;
    private final BookingRepository bookingRepository;
    private static final String JOB_KEY_PREFIX = "service:%d:jobs";
    private static final long JOB_EXPIRATION_MINUTES = 5;
    private final UserService userService;
//    private final WorkerService workerService;
    @Override
    public void findWorkers(FindServiceRequestDTO request, List<MultipartFile> files) {
        ServiceWG service = serviceService.getServiceByIdEntity(request.getServiceId());
        UserResponseDTO user = userService.getUserById(SecurityUtils.getCurrentUserId());
        List<String> imageUrls = Optional.ofNullable(files)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(uploadToS3::uploadFileToS3)
                .filter(Objects::nonNull)
                .map(UploadS3Response::getFileUrl)
                .filter(Objects::nonNull)
                .toList();

        JobRequestResponseDTO jobRequest = JobRequestResponseDTO.builder()
                .serviceName(service.getServiceName())
                .description(request.getDescription())
                .bookingDate(request.getBookingDate())
                .estimatedPrice(request.getEstimatedPrice())
                .distance(request.getDistance())
                .fileUrls(imageUrls)
                .user(user)
                .build();

        String key = String.format(JOB_KEY_PREFIX, service.getId());
        redisTemplate.opsForList().rightPush(key, jobRequest);
        redisTemplate.expire(key, JOB_EXPIRATION_MINUTES, TimeUnit.MINUTES);

//        //save booking with status PENDING
//        saveBooking(BookingRequestDTO.builder()
//                .userId(SecurityUtils.getCurrentUserId())
//                .service(service)
//                .bookingDate(LocalDateTime.now())
//                .description(request.getDescription())
//                .distanceKm(request.getDistance())
//                .bookingAddress(request.getAddress())
//                .build());
    }

    @Override
    public List<JobRequestResponseDTO> getJobRequestsByListServiceId(List<Long> serviceIds) {
        if (serviceIds == null || serviceIds.isEmpty()) {
            return Collections.emptyList();
        }

        return serviceIds.stream()
                .map(serviceId -> String.format(JOB_KEY_PREFIX, serviceId))
                .map(key -> redisTemplate.opsForList().range(key, 0, -1))
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .map(o -> (JobRequestResponseDTO) o)
                .toList();
    }

    @Override
    public Booking saveBooking(BookingRequestDTO request) {
        return bookingRepository.save(createBooking(request));
    }

    private Booking createBooking(BookingRequestDTO request) {
        return Booking.builder()
                .bookingCode(generateBookingCode())
                .service(request.getService())
                .user(request.getUser())
                .worker(null) // Chưa gán worker lúc tạo booking
                .bookingDate(request.getBookingDate())
                .startDate(null)
                .endDate(null)
                .description(request.getDescription())
                .distanceKm(request.getDistanceKm())
                .bookingStatus(BookingStatus.PENDING)
                .durationMinutes(0)
                .title(request.getService().getServiceName())
                .bookingAddress(request.getBookingAddress())
                .build();
    }
    private String generateBookingCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return "BK-" + uuid.substring(0, 8) + "-" + LocalDateTime.now().getYear();
    }

}
