package com.localfix.booking.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CreateBookingRequest(

        @NotNull(message = "Provider ID is required")
        UUID providerId,

        @NotNull(message = "Service ID is required")
        UUID serviceId,

        @NotNull(message = "Booking date is required")
        @FutureOrPresent(
                message = "Booking date cannot be in the past"
        )
        LocalDate bookingDate,

        @NotNull(message = "Booking time is required")
        LocalTime bookingTime,

        @NotBlank(message = "Address is required")
        @Size(max = 500)
        String address,

        @NotBlank(message = "City is required")
        @Size(max = 100)
        String city,

        @NotBlank(message = "State is required")
        @Size(max = 100)
        String state,

        @Size(max = 10)
        String pincode,

        @Size(max = 1000)
        String customerNote

) {
}