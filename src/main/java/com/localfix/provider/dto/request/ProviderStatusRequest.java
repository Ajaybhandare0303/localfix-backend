package com.localfix.provider.dto.request;

import jakarta.validation.constraints.NotNull;

public record ProviderStatusRequest(

        @NotNull(message = "Active status is required")
        Boolean active

) {
}