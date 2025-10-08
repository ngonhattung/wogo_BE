package com.nhattung.wogo.service.review;

import com.nhattung.wogo.dto.request.ReviewRequestDTO;
import com.nhattung.wogo.entity.Booking;
import com.nhattung.wogo.entity.Review;
import com.nhattung.wogo.repository.ReviewRepository;
import com.nhattung.wogo.service.booking.IBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {

    private final ReviewRepository reviewRepository;
    private final IBookingService bookingService;

    @Override
    public Review saveReview(ReviewRequestDTO request) {
        Booking booking = bookingService.getBookingById(request.getBookingId());
        Review review = Review.builder()
                .booking(booking)
                .worker(booking.getWorker())
                .rating(request.getRating())
                .content(request.getComment())
                .user(booking.getUser())
                .build();
        return reviewRepository.save(review);
    }
}
