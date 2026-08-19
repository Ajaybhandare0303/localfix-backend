package com.localfix.booking.dto.response;

import lombok.Builder;

@Builder
public record BookingDashboardResponse(

        long totalBookings,

        long pendingBookings,

        long confirmedBookings,

        long providerOnTheWayBookings,

        long inProgressBookings,

        long completedBookings,

        long rejectedBookings,

        long cancelledByCustomerBookings,

        long cancelledByProviderBookings

) {
}