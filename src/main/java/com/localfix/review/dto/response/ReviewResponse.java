package com.localfix.review.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ReviewResponse(

        UUID id,

        UUID bookingId,

        UUID customerId,

        UUID providerId,

        Integer rating,

        String comment,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}