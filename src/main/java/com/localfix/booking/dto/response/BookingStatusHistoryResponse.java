package com.localfix.booking.dto.response;

import com.localfix.booking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingStatusHistoryResponse(

        UUID id,

        BookingStatus oldStatus,

        BookingStatus newStatus,

        UUID changedBy,

        String changedByName,

        LocalDateTime changedAt,

        String reason

) {
}