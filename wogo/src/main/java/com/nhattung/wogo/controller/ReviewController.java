package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.request.ReviewRequestDTO;
import com.nhattung.wogo.dto.request.UpdateWorkerRequestDTO;
import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.entity.Review;
import com.nhattung.wogo.service.review.IReviewService;
import com.nhattung.wogo.service.worker.IWorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final IReviewService reviewService;
    private final IWorkerService workerService;


    @PostMapping("/create")
    public ApiResponse<Void> createReview(@RequestBody ReviewRequestDTO request) {
        Review review = reviewService.saveReview(request);
        workerService.updateWorker(UpdateWorkerRequestDTO.builder()
                        .rating(review.getRating())
                .build(), review.getWorker());
        return ApiResponse.<Void>builder()
                .message("Review created successfully")
                .build();
    }


}
