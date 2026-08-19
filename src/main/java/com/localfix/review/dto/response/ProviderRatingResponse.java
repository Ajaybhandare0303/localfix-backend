package com.localfix.review.dto.response;

import lombok.Builder;

@Builder
public record ProviderRatingResponse(

        Double averageRating,

        Long totalReviews,

        Long fiveStarReviews,

        Long fourStarReviews,

        Long threeStarReviews,

        Long twoStarReviews,

        Long oneStarReviews

) {
}