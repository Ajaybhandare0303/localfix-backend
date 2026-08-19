package com.localfix.review.controller;

import com.localfix.common.response.ApiResponse;
import com.localfix.review.dto.request.CreateReviewRequest;
import com.localfix.review.dto.request.UpdateReviewRequest;
import com.localfix.review.dto.response.ProviderRatingResponse;
import com.localfix.review.dto.response.ReviewResponse;
import com.localfix.review.service.ReviewService;

import com.localfix.servicecategory.dto.response.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;


    @PostMapping
    public ResponseEntity<
            ApiResponse<ReviewResponse>>
    createReview(
            @Valid
            @RequestBody
            CreateReviewRequest request) {

        ReviewResponse response =
                reviewService.createReview(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse
                                .<ReviewResponse>builder()
                                .success(true)
                                .message(
                                        "Review submitted successfully."
                                )
                                .data(response)
                                .build()
                );
    }


    @GetMapping("/provider/{providerId}")
    public ResponseEntity<
            ApiResponse<Page<ReviewResponse>>>
    getProviderReviews(

            @PathVariable UUID providerId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        Page<ReviewResponse> response =
                reviewService.getProviderReviews(
                        providerId,
                        page,
                        size
                );

        return ResponseEntity.ok(
                ApiResponse
                        .<Page<ReviewResponse>>builder()
                        .success(true)
                        .message(
                                "Provider reviews retrieved successfully."
                        )
                        .data(response)
                        .build()
        );
    }


    @GetMapping("/provider/{providerId}/rating")
    public ResponseEntity<
            ApiResponse<ProviderRatingResponse>>
    getProviderRating(
            @PathVariable UUID providerId) {

        ProviderRatingResponse response =
                reviewService.getProviderRating(
                        providerId
                );

        return ResponseEntity.ok(
                ApiResponse
                        .<ProviderRatingResponse>builder()
                        .success(true)
                        .message(
                                "Provider rating retrieved successfully."
                        )
                        .data(response)
                        .build()
        );
    }


    @GetMapping("/my")
    public ResponseEntity<
            ApiResponse<Page<ReviewResponse>>>
    getMyReviews(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        Page<ReviewResponse> response =
                reviewService.getMyReviews(
                        page,
                        size
                );

        return ResponseEntity.ok(
                ApiResponse
                        .<Page<ReviewResponse>>builder()
                        .success(true)
                        .message(
                                "My reviews retrieved successfully."
                        )
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<
            ApiResponse<ReviewResponse>>
    updateReview(

            @PathVariable UUID reviewId,

            @Valid
            @RequestBody
            UpdateReviewRequest request) {

        ReviewResponse response =
                reviewService.updateReview(
                        reviewId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse
                        .<ReviewResponse>builder()
                        .success(true)
                        .message(
                                "Review updated successfully."
                        )
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<
            ApiResponse<Void>>
    deleteReview(
            @PathVariable UUID reviewId) {

        reviewService.deleteReview(reviewId);

        return ResponseEntity.ok(
                ApiResponse
                        .<Void>builder()
                        .success(true)
                        .message(
                                "Review deleted successfully."
                        )
                        .build()
        );
    }
}