package com.localfix.service.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ServiceResponse(

        UUID id,

        UUID categoryId,

        String categoryName,

        String name,

        String description,

        Integer estimatedDuration,

        BigDecimal basePrice,

        Boolean active

) {
}