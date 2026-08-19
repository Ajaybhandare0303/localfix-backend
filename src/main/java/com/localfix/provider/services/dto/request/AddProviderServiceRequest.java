package com.localfix.provider.services.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddProviderServiceRequest(

        @NotNull(message = "Service ID is required")
        UUID serviceId

) {
}