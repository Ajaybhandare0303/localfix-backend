package com.localfix.provider.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ProviderListResponse(

        UUID id,

        String businessName,

        String description,

        String city,

        String state,

        String experience,

        Boolean verified

) {
}