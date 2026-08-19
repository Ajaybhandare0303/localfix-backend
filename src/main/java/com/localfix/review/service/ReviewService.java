package com.localfix.review.service;

import com.localfix.review.dto.request.CreateReviewRequest;
import com.localfix.review.dto.request.UpdateReviewRequest;
import com.localfix.review.dto.response.ProviderRatingResponse;
import com.localfix.review.dto.response.ReviewResponse;
import com.localfix.servicecategory.dto.response.PageResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ReviewService {

    ReviewResponse createReview(
            CreateReviewRequest request
    );

    Page<ReviewResponse> getProviderReviews(
            UUID providerId,
            int page,
            int size
    );

    ProviderRatingResponse getProviderRating(
            UUID providerId
    );

    Page<ReviewResponse> getMyReviews(
            int page,
            int size
    );

    ReviewResponse getReviewById(UUID reviewId);

    ReviewResponse updateReview(
            UUID reviewId,
            UpdateReviewRequest request
    );

    void deleteReview(UUID reviewId);




}