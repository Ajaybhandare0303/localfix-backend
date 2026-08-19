package com.localfix.provider.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProviderRequest(

        @NotBlank(message = "Business name is required")
        @Size(
                max = 150,
                message = "Business name must not exceed 150 characters"
        )
        String businessName,

        @Size(
                max = 1000,
                message = "Description must not exceed 1000 characters"
        )
        String description,

        @Size(
                max = 500,
                message = "Address must not exceed 500 characters"
        )
        String address,

        @Size(max = 100)
        String city,

        @Size(max = 100)
        String state,

        @Size(max = 10)
        String pincode,

        @Size(max = 20)
        String experience
) {
}