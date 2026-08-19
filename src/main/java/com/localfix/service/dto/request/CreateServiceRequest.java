package com.localfix.service.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateServiceRequest(

        @NotNull(message = "Category is required")
        UUID categoryId,

        @NotBlank(message = "Service name is required")
        @Size(max = 100, message = "Service name must not exceed 100 characters")
        String name,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @NotNull(message = "Estimated duration is required")
        @Min(value = 1, message = "Duration must be greater than 0")
        Integer estimatedDuration,

        @NotNull(message = "Base price is required")
        @DecimalMin(value = "0.0", inclusive = false,
                message = "Base price must be greater than 0")
        @Digits(integer = 8, fraction = 2,
                message = "Invalid price format")
        BigDecimal basePrice

) {
}