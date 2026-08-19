package com.localfix.booking.dto.response;

import com.localfix.booking.enums.BookingStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Builder
public record BookingResponse(

        UUID id,

        UUID customerId,

        UUID providerId,

        String providerName,

        UUID serviceId,

        String serviceName,

        LocalDate bookingDate,

        LocalTime bookingTime,

        String address,

        String city,

        String state,

        String pincode,

        String customerNote,

        BookingStatus status,

        LocalDateTime createdAt

) {
}