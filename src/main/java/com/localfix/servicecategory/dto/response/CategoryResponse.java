package com.localfix.servicecategory.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CategoryResponse(

        UUID id,

        String name,

        String description,

        String icon,

        Boolean active

) {
}