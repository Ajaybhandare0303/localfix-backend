package com.localfix.provider.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ProviderResponse(

        UUID id,

        UUID userId,

        String businessName,

        String description,

        String address,

        String city,

        String state,

        String pincode,

        String experience,

        Boolean active,

        Boolean verified

) {
}