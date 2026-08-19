package com.localfix.provider.services.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ProviderServiceResponse(

        UUID id,

        UUID providerId,

        UUID serviceId,

        String serviceName,

        UUID categoryId,

        String categoryName,

        BigDecimal basePrice,

        Integer estimatedDuration,

        Boolean active

) {
}