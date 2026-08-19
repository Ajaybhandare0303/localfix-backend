package com.localfix.booking.dto.request;

import com.localfix.booking.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateBookingStatusRequest(

        @NotNull(message = "Booking status is required")
        BookingStatus status

) {
}