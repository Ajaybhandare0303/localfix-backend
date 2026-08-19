package com.localfix.notification.service;

import com.localfix.booking.entity.Booking;

public interface BookingEmailNotificationService {

    void sendBookingCreatedEmail(Booking booking);

    void sendBookingConfirmedEmail(Booking booking);

    void sendBookingRejectedEmail(Booking booking);

    void sendProviderOnTheWayEmail(Booking booking);

    void sendBookingStartedEmail(Booking booking);

    void sendBookingCompletedEmail(Booking booking);

    void sendBookingCancelledEmail(Booking booking);

    void sendBookingRescheduledEmail(Booking booking);
}