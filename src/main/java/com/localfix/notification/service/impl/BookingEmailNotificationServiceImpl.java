package com.localfix.notification.service.impl;

import com.localfix.auth.email.EmailService;
import com.localfix.booking.entity.Booking;
import com.localfix.notification.service.BookingEmailNotificationService;
import com.localfix.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingEmailNotificationServiceImpl
        implements BookingEmailNotificationService {

    private final EmailService emailService;

    @Override
    public void sendBookingCreatedEmail(
            Booking booking) {

        User providerUser =
                booking.getProvider().getUser();

        String email =
                providerUser.getEmail();

        String subject =
                "LocalFix - New Booking Request";

        String body =
                buildBookingCreatedEmail(booking);

        emailService.sendSimpleEmail(
                email,
                subject,
                body
        );
    }

    @Override
    public void sendBookingConfirmedEmail(
            Booking booking) {

        String email =
                booking.getCustomer().getEmail();

        emailService.sendSimpleEmail(
                email,
                "LocalFix - Booking Confirmed",
                buildBookingConfirmedEmail(booking)
        );
    }

    @Override
    public void sendBookingRejectedEmail(
            Booking booking) {

        String email =
                booking.getCustomer().getEmail();

        emailService.sendSimpleEmail(
                email,
                "LocalFix - Booking Rejected",
                buildBookingRejectedEmail(booking)
        );
    }

    @Override
    public void sendProviderOnTheWayEmail(
            Booking booking) {

        String email =
                booking.getCustomer().getEmail();

        emailService.sendSimpleEmail(
                email,
                "LocalFix - Provider On The Way",
                buildProviderOnTheWayEmail(booking)
        );
    }

    @Override
    public void sendBookingStartedEmail(
            Booking booking) {

        String email =
                booking.getCustomer().getEmail();

        emailService.sendSimpleEmail(
                email,
                "LocalFix - Service Started",
                buildBookingStartedEmail(booking)
        );
    }

    @Override
    public void sendBookingCompletedEmail(
            Booking booking) {

        String email =
                booking.getCustomer().getEmail();

        emailService.sendSimpleEmail(
                email,
                "LocalFix - Booking Completed",
                buildBookingCompletedEmail(booking)
        );
    }

    @Override
    public void sendBookingCancelledEmail(
            Booking booking) {

        String customerEmail =
                booking.getCustomer().getEmail();

        emailService.sendSimpleEmail(
                customerEmail,
                "LocalFix - Booking Cancelled",
                buildBookingCancelledEmail(booking)
        );
    }

    @Override
    public void sendBookingRescheduledEmail(
            Booking booking) {

        String customerEmail =
                booking.getCustomer().getEmail();

        emailService.sendSimpleEmail(
                customerEmail,
                "LocalFix - Booking Rescheduled",
                buildBookingRescheduledEmail(booking)
        );
    }

    private String buildBookingCreatedEmail(
            Booking booking) {

        return """
            Hello Provider,

            You have received a new booking request on LocalFix.

            Booking ID: %s
            Date: %s
            Time: %s
            Status: %s

            Please login to your LocalFix account to review
            and respond to this booking.

            Regards,
            LocalFix Team
            """.formatted(
                booking.getId(),
                booking.getBookingDate(),
                booking.getBookingTime(),
                booking.getStatus()
        );
    }

    private String buildBookingConfirmedEmail(
            Booking booking) {

        return """
            Hello,

            Good news! Your LocalFix booking has been confirmed.

            Booking ID: %s
            Date: %s
            Time: %s
            Status: CONFIRMED

            Please be available at the scheduled time.

            Regards,
            LocalFix Team
            """.formatted(
                booking.getId(),
                booking.getBookingDate(),
                booking.getBookingTime()
        );
    }

    private String buildBookingRejectedEmail(
            Booking booking) {

        return """
            Hello,

            Unfortunately, your LocalFix booking request was rejected.

            Booking ID: %s
            Date: %s
            Time: %s

            You can search for another available service provider
            through LocalFix.

            Regards,
            LocalFix Team
            """.formatted(
                booking.getId(),
                booking.getBookingDate(),
                booking.getBookingTime()
        );
    }

    private String buildBookingStartedEmail(
            Booking booking) {

        return """
            Hello,

            Your LocalFix service has started.

            Booking ID: %s

            Thank you for using LocalFix.

            Regards,
            LocalFix Team
            """.formatted(
                booking.getId()
        );
    }

    private String buildBookingCancelledEmail(
            Booking booking) {

        return """
            Hello,

            Your LocalFix booking has been cancelled.

            Booking ID: %s
            Date: %s
            Time: %s

            If you need the service, you can create a new booking
            with another available provider.

            Regards,
            LocalFix Team
            """.formatted(
                booking.getId(),
                booking.getBookingDate(),
                booking.getBookingTime()
        );
    }

    private String buildBookingRescheduledEmail(
            Booking booking) {

        return """
            Hello,

            Your LocalFix booking has been rescheduled.

            Booking ID: %s
            New Date: %s
            New Time: %s

            Please make sure you are available at the new
            scheduled time.

            Regards,
            LocalFix Team
            """.formatted(
                booking.getId(),
                booking.getBookingDate(),
                booking.getBookingTime()
        );
    }

    private String buildProviderOnTheWayEmail(
            Booking booking) {

        return """
            Hello,

            Your service provider is on the way.

            Booking ID: %s
            Date: %s
            Time: %s

            Please be available at the service location.

            Regards,
            LocalFix Team
            """.formatted(
                booking.getId(),
                booking.getBookingDate(),
                booking.getBookingTime()
        );
    }

    private String buildBookingCompletedEmail(
            Booking booking) {

        return """
            Hello,

            Your LocalFix booking has been completed successfully.

            Booking ID: %s
            Date: %s

            We hope you had a great experience.

            Please consider leaving a rating and review
            for your service provider.

            Regards,
            LocalFix Team
            """.formatted(
                booking.getId(),
                booking.getBookingDate()
        );
    }
}