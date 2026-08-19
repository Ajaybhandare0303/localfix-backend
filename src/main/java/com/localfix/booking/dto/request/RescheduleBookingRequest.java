package com.localfix.booking.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record RescheduleBookingRequest(

        @NotNull(message = "Booking date is required")
        @FutureOrPresent(message = "Booking date must be today or in the future")
        LocalDate bookingDate,

        @NotNull(message = "Booking time is required")
        LocalTime bookingTime

) {
}