package com.nhattung.wogo.service.review;

import com.nhattung.wogo.dto.request.ReviewRequestDTO;
import com.nhattung.wogo.dto.response.ReviewResponseDTO;
import com.nhattung.wogo.entity.Review;

public interface IReviewService {

    Review saveReview(ReviewRequestDTO request);
    ReviewResponseDTO getReviewById(Long id);
}
