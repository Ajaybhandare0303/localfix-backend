package com.localfix.booking.service.impl;

import com.localfix.auth.email.EmailService;
import com.localfix.booking.dto.request.CreateBookingRequest;
import com.localfix.booking.dto.request.RescheduleBookingRequest;
import com.localfix.booking.dto.request.UpdateBookingStatusRequest;
import com.localfix.booking.dto.response.BookingDashboardResponse;
import com.localfix.booking.dto.response.BookingResponse;
import com.localfix.booking.dto.response.BookingStatusHistoryResponse;
import com.localfix.booking.entity.Booking;
import com.localfix.booking.entity.BookingStatusHistory;
import com.localfix.notification.enums.NotificationType;
import com.localfix.notification.service.BookingEmailNotificationService;
import com.localfix.notification.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import com.localfix.booking.enums.BookingStatus;
import com.localfix.booking.repository.BookingRepository;
import com.localfix.booking.repository.BookingStatusHistoryRepository;
import com.localfix.booking.service.BookingService;
import com.localfix.common.exception.ResourceNotFoundException;
import com.localfix.provider.entity.Provider;
import com.localfix.provider.repository.ProviderRepository;
import com.localfix.provider.services.repository.ProviderServiceRepository;
import com.localfix.service.repository.ServiceRepository;
import com.localfix.servicecategory.dto.response.PageResponse;
import com.localfix.user.entity.User;
import com.localfix.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ProviderRepository providerRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final ProviderServiceRepository providerServiceRepository;
    private final BookingStatusHistoryRepository bookingStatusHistoryRepository;
    private final BookingEmailNotificationService bookingEmailNotificationService;
    private final NotificationService notificationService;


    @Override
    public BookingResponse createBooking(CreateBookingRequest request) {

        User customer =
                getAuthenticatedUser();

        Provider provider =
                providerRepository
                        .findByIdAndActiveTrueAndVerifiedTrue(
                                request.providerId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Provider not found."
                                ));

        com.localfix.service.entity.Service service =
                serviceRepository
                        .findById(request.serviceId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Service not found."
                                ));


        validateProviderOffersService(
                provider.getId(),
                service.getId()
        );

        validateBookingDateTime(
                request.bookingDate(),
                request.bookingTime()
        );

        List<BookingStatus> activeStatuses =
                List.of(
                        BookingStatus.PENDING,
                        BookingStatus.CONFIRMED,
                        BookingStatus.PROVIDER_ON_THE_WAY,
                        BookingStatus.IN_PROGRESS
                );

        boolean alreadyBooked =
                bookingRepository
                        .existsByProviderIdAndBookingDateAndBookingTimeAndStatusIn(
                                provider.getId(),
                                request.bookingDate(),
                                request.bookingTime(),
                                activeStatuses
                        );

        if (alreadyBooked) {
            throw new IllegalStateException(
                    "Provider is already booked at the selected date and time."
            );
        }

        Booking booking =
                Booking.builder()
                        .customer(customer)
                        .provider(provider)
                        .service(service)
                        .bookingDate(
                                request.bookingDate()
                        )
                        .bookingTime(
                                request.bookingTime()
                        )
                        .address(
                                request.address()
                        )
                        .city(
                                request.city()
                        )
                        .state(
                                request.state()
                        )
                        .pincode(
                                request.pincode()
                        )
                        .customerNote(
                                request.customerNote()
                        )
                        .status(
                                BookingStatus.PENDING
                        )
                        .build();

        Booking savedBooking =
                bookingRepository.save(booking);

        try {
            bookingEmailNotificationService
                    .sendBookingCreatedEmail(
                            savedBooking
                    );
        }catch (Exception ex) {

            log.error(
                    "Failed to send booking email for booking {}",
                    savedBooking.getId(),
                    ex
            );
        }

        saveStatusHistory(
                savedBooking,
                null,
                BookingStatus.PENDING,
                customer,
                "Booking created"
        );

        notificationService.createNotification(
                provider.getUser().getId(),
                NotificationType.BOOKING_CREATED,
                "New Booking",
                "You have received a new booking request.",
                savedBooking.getId()
        );

        return mapToResponse(savedBooking);
    }

    private void sendBookingEmail(
            Booking booking,
            BookingStatus status) {

        switch (status) {

            case CONFIRMED ->
                    bookingEmailNotificationService
                            .sendBookingConfirmedEmail(
                                    booking
                            );

            case REJECTED ->
                    bookingEmailNotificationService
                            .sendBookingRejectedEmail(
                                    booking
                            );

            case PROVIDER_ON_THE_WAY ->
                    bookingEmailNotificationService
                            .sendProviderOnTheWayEmail(
                                    booking
                            );

            case IN_PROGRESS ->
                    bookingEmailNotificationService
                            .sendBookingStartedEmail(
                                    booking
                            );

            case COMPLETED ->
                    bookingEmailNotificationService
                            .sendBookingCompletedEmail(
                                    booking
                            );

            case CANCELLED_BY_CUSTOMER,
                 CANCELLED_BY_PROVIDER ->
                    bookingEmailNotificationService
                            .sendBookingCancelledEmail(
                                    booking
                            );

            default -> {
                // No email required
            }
        }
    }


    private void validateProviderOffersService(
            UUID providerId,
            UUID serviceId) {

        boolean serviceOffered =
                providerServiceRepository
                        .existsByProviderIdAndServiceIdAndActiveTrue(
                                providerId,
                                serviceId
                        );

        if (!serviceOffered) {
            throw new ResourceNotFoundException(
                    "Selected service is not offered by this provider."
            );
        }
    }


    private User getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email =
                authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found."
                        ));
    }

    private void validateBookingDateTime(
            LocalDate bookingDate,
            LocalTime bookingTime) {

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (bookingDate.isBefore(today)) {
            throw new IllegalArgumentException(
                    "Booking date cannot be in the past."
            );
        }

        if (bookingDate.equals(today)
                && bookingTime.isBefore(now)) {

            throw new IllegalArgumentException(
                    "Booking time cannot be in the past."
            );
        }
    }

    private BookingResponse mapToResponse(
            Booking booking) {

        Provider provider =
                booking.getProvider();

        com.localfix.service.entity.Service service =
                booking.getService();

        User customer =
                booking.getCustomer();

        return BookingResponse
                .builder()
                .id(booking.getId())
                .customerId(customer.getId())
                .providerId(provider.getId())
                .providerName(
                        provider.getBusinessName()
                )
                .serviceId(service.getId())
                .serviceName(service.getName())
                .bookingDate(
                        booking.getBookingDate()
                )
                .bookingTime(
                        booking.getBookingTime()
                )
                .address(
                        booking.getAddress()
                )
                .city(
                        booking.getCity()
                )
                .state(
                        booking.getState()
                )
                .pincode(
                        booking.getPincode()
                )
                .customerNote(
                        booking.getCustomerNote()
                )
                .status(
                        booking.getStatus()
                )
                .createdAt(
                        booking.getCreatedAt()
                )
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(
            UUID bookingId) {

        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Booking not found."
                                ));

        User user =
                getAuthenticatedUser();

        boolean isCustomer =
                booking.getCustomer()
                        .getId()
                        .equals(user.getId());

        boolean isProvider =
                booking.getProvider()
                        .getUser()
                        .getId()
                        .equals(user.getId());

        if (!isCustomer && !isProvider) {

            throw new AccessDeniedException(
                    "You are not authorized to view this booking."
            );
        }

        return mapToResponse(booking);
    }

    @Override
    public PageResponse<BookingResponse> getMyBookings(int page, int size) {



        return null;
    }

    @Override
    public PageResponse<BookingResponse> getProviderBookings(int page, int size) {
        return null;
    }

    @Override
    public BookingResponse updateBookingStatus(UUID bookingId, UpdateBookingStatusRequest request) {

        Booking booking=bookingRepository.findById(bookingId).orElseThrow(()->
                new ResourceNotFoundException("Booking not found with id : "+bookingId)
                );

        User user=getAuthenticatedUser();

        BookingStatus currentStatus =
                booking.getStatus();

        BookingStatus requestedStatus =
                request.status();

        if (booking.getStatus() == requestedStatus) {

            throw new IllegalStateException(
                    "Booking is already in this status."
            );
        }

        validateStatusTransition(
                booking,
                user,
                currentStatus,
                requestedStatus
        );

        BookingStatus oldStatus =
                booking.getStatus();

        booking.setStatus(requestedStatus);

        Booking savedBooking =
                bookingRepository.save(booking);

        try {
            sendBookingEmail(
                    savedBooking,
                    requestedStatus
            );
        }catch (Exception ex) {

            log.error(
                    "Failed to send email for booking status update  {}",
                    savedBooking.getId(),
                    ex
            );
        }

        sendBookingStatusNotification(
                savedBooking,
                requestedStatus
        );

        saveStatusHistory(
                savedBooking,
                oldStatus,
                requestedStatus,
                user,
                "Booking status updated"
        );


        return mapToResponse(savedBooking);
    }

    private void validateStatusTransition(
            Booking booking,
            User user,
            BookingStatus currentStatus,
            BookingStatus requestedStatus) {

        boolean isCustomer =
                booking.getCustomer()
                        .getId()
                        .equals(user.getId());

        boolean isProvider =
                booking.getProvider()
                        .getUser()
                        .getId()
                        .equals(user.getId());

        if (!isCustomer && !isProvider) {

            throw new IllegalStateException(
                    "You are not authorized to update this booking."
            );
        }

        if (isCustomer) {

            if (requestedStatus
                    != BookingStatus.CANCELLED_BY_CUSTOMER) {

                throw new IllegalStateException(
                        "Customer can only cancel a booking."
                );
            }

            if (currentStatus == BookingStatus.COMPLETED) {

                throw new IllegalStateException(
                        "Completed booking cannot be cancelled."
                );
            }

            if (currentStatus == BookingStatus.CANCELLED_BY_CUSTOMER
                    || currentStatus == BookingStatus.CANCELLED_BY_PROVIDER
                    || currentStatus == BookingStatus.REJECTED) {

                throw new IllegalStateException(
                        "Booking is already closed."
                );
            }

            return;
        }

        /*
         * Provider status transitions
         */

        if (currentStatus == BookingStatus.PENDING) {

            if (requestedStatus != BookingStatus.CONFIRMED
                    && requestedStatus != BookingStatus.REJECTED) {

                throw new IllegalStateException(
                        "Pending booking can only be confirmed or rejected."
                );
            }

            return;
        }

        if (currentStatus == BookingStatus.CONFIRMED) {

            if (requestedStatus != BookingStatus.PROVIDER_ON_THE_WAY
                    && requestedStatus
                    != BookingStatus.CANCELLED_BY_PROVIDER) {

                throw new IllegalStateException(
                        "Confirmed booking can only be marked as provider on the way or cancelled."
                );
            }

            return;
        }

        if (currentStatus
                == BookingStatus.PROVIDER_ON_THE_WAY) {

            if (requestedStatus
                    != BookingStatus.IN_PROGRESS) {

                throw new IllegalStateException(
                        "Provider on the way booking can only move to in progress."
                );
            }

            return;
        }

        if (currentStatus
                == BookingStatus.IN_PROGRESS) {

            if (requestedStatus
                    != BookingStatus.COMPLETED) {

                throw new IllegalStateException(
                        "In-progress booking can only be completed."
                );
            }

            return;
        }

        throw new IllegalStateException(
                "Invalid booking status transition."
        );
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(
            UUID bookingId) {

        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Booking not found."
                                ));

        User customer =
                getAuthenticatedUser();

        if (!booking.getCustomer()
                .getId()
                .equals(customer.getId())) {

            throw new IllegalStateException(
                    "You are not authorized to cancel this booking."
            );
        }

        if (booking.getStatus()
                == BookingStatus.COMPLETED) {

            throw new IllegalStateException(
                    "Completed booking cannot be cancelled."
            );
        }

        if (booking.getStatus()
                == BookingStatus.CANCELLED_BY_CUSTOMER
                || booking.getStatus()
                == BookingStatus.CANCELLED_BY_PROVIDER
                || booking.getStatus()
                == BookingStatus.REJECTED) {

            throw new IllegalStateException(
                    "Booking is already closed."
            );
        }

        validateCancellationWindow(booking);

        BookingStatus oldStatus=booking.getStatus();

        booking.setStatus(
                BookingStatus.CANCELLED_BY_CUSTOMER
        );

        Booking savedBooking =
                bookingRepository.save(booking);

        saveStatusHistory(
                savedBooking,
                oldStatus,
                BookingStatus.CANCELLED_BY_CUSTOMER,
                customer,
                "Booking cancelled by customer"
        );

        return mapToResponse(savedBooking);
    }

    private void validateCancellationWindow(
            Booking booking) {

        LocalDateTime bookingDateTime =
                LocalDateTime.of(
                        booking.getBookingDate(),
                        booking.getBookingTime()
                );

        LocalDateTime cancellationDeadline =
                bookingDateTime.minusHours(2);

        if (LocalDateTime.now()
                .isAfter(cancellationDeadline)) {

            throw new IllegalStateException(
                    "Booking cannot be cancelled within 2 hours of the scheduled time."
            );
        }
    }

    private void validateRescheduleWindow(
            Booking booking) {

        LocalDateTime bookingDateTime =
                LocalDateTime.of(
                        booking.getBookingDate(),
                        booking.getBookingTime()
                );

        LocalDateTime deadline =
                bookingDateTime.minusHours(2);

        if (LocalDateTime.now()
                .isAfter(deadline)) {

            throw new IllegalStateException(
                    "Booking cannot be rescheduled within 2 hours of the scheduled time."
            );
        }
    }

    @Override
    public List<BookingStatusHistoryResponse> getBookingStatusHistory(UUID bookingId) {

        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Booking not found."
                                ));

        User user=getAuthenticatedUser();

        boolean isCustomer =
                booking.getCustomer()
                        .getId()
                        .equals(user.getId());

        boolean isProvider =
                booking.getProvider()
                        .getUser()
                        .getId()
                        .equals(user.getId());

        if (!isCustomer && !isProvider) {
            throw new IllegalStateException(
                    "You are not authorized to view this booking."
            );
        }

        return bookingStatusHistoryRepository.findAllByBookingIdOrderByChangedAtAsc(bookingId)
                .stream()
                .map(this::mapStatusHistory)
                .toList();
    }

    @Override
    @Transactional
    public BookingResponse rescheduleBooking(
            UUID bookingId,
            RescheduleBookingRequest request) {

        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Booking not found."
                                ));

        User customer = getAuthenticatedUser();

        if (!booking.getCustomer()
                .getId()
                .equals(customer.getId())) {

            throw new IllegalStateException(
                    "You are not authorized to reschedule this booking."
            );
        }

        BookingStatus currentStatus =
                booking.getStatus();

        if (currentStatus != BookingStatus.PENDING
                && currentStatus != BookingStatus.CONFIRMED) {

            throw new IllegalStateException(
                    "Booking cannot be rescheduled in its current status."
            );
        }

        validateBookingDateTime(
                request.bookingDate(),
                request.bookingTime()
        );

        boolean alreadyBooked =
                bookingRepository
                        .existsByProviderIdAndBookingDateAndBookingTimeAndStatusInAndIdNot(
                                booking.getProvider().getId(),
                                request.bookingDate(),
                                request.bookingTime(),
                                List.of(
                                        BookingStatus.PENDING,
                                        BookingStatus.CONFIRMED,
                                        BookingStatus.PROVIDER_ON_THE_WAY,
                                        BookingStatus.IN_PROGRESS
                                ),
                                bookingId
                        );

        if (alreadyBooked) {

            throw new IllegalStateException(
                    "Provider is already booked at the selected date and time."
            );
        }

        LocalDate oldDate =
                booking.getBookingDate();

        validateRescheduleWindow(booking);

        LocalTime oldTime =
                booking.getBookingTime();

        booking.setBookingDate(
                request.bookingDate()
        );

        booking.setBookingTime(
                request.bookingTime()
        );

        Booking savedBooking =
                bookingRepository.save(booking);

        try {
            bookingEmailNotificationService
                    .sendBookingRescheduledEmail(
                            savedBooking
                    );
        }catch (Exception ex) {

            log.error(
                    "Failed to send email for reSchedule booking {}",
                    savedBooking.getId(),
                    ex
            );
        }

        notificationService.createNotification(
                booking.getCustomer().getId(),
                NotificationType.BOOKING_RESCHEDULED,
                "Booking Rescheduled",
                "Your booking has been rescheduled successfully.",
                booking.getId()
        );

        notificationService.createNotification(
                booking.getProvider().getUser().getId(),
                NotificationType.BOOKING_RESCHEDULED,
                "Booking Rescheduled",
                "A booking has been rescheduled.",
                booking.getId()
        );

        saveStatusHistory(
                savedBooking,
                currentStatus,
                currentStatus,
                customer,
                "Booking rescheduled from "
                        + oldDate
                        + " "
                        + oldTime
                        + " to "
                        + request.bookingDate()
                        + " "
                        + request.bookingTime()
        );

        return mapToResponse(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookingResponse> getMyBookings(BookingStatus status, int page, int size) {

        User customer=getAuthenticatedUser();

        Pageable pageable= PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC,
                        "bookingDate"
                        ).and(
                                Sort.by(
                                        Sort.Direction.DESC,
                                        "bookingTime"
                                )
                )

        );

        Page<Booking> bookings;

        if(status != null)
        {
            bookings=bookingRepository
                    .findAllByCustomerIdAndStatusOrderByBookingDateDescBookingTimeDesc(
                            customer.getId(),
                            status,
                            pageable
                    );
        }else {
            bookings =
                    bookingRepository
                            .findAllByCustomerIdOrderByBookingDateDescBookingTimeDesc(
                                    customer.getId(),
                                    pageable
                            );
        }

        return mapToPageResponse(bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookingResponse> getProviderBookings(
            BookingStatus status,
            int page,
            int size) {

        Provider provider =
                getAuthenticatedProvider();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.DESC,
                                "bookingDate"
                        ).and(
                                Sort.by(
                                        Sort.Direction.DESC,
                                        "bookingTime"
                                )
                        )
                );

        Page<Booking> bookings;

        if (status != null) {

            bookings =
                    bookingRepository
                            .findAllByProviderIdAndStatusOrderByBookingDateDescBookingTimeDesc(
                                    provider.getId(),
                                    status,
                                    pageable
                            );

        } else {

            bookings =
                    bookingRepository
                            .findAllByProviderIdOrderByBookingDateDescBookingTimeDesc(
                                    provider.getId(),
                                    pageable
                            );
        }

        return mapToPageResponse(bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDashboardResponse getMyDashboard() {

        User customer =
                getAuthenticatedUser();

        UUID customerId =
                customer.getId();

        return BookingDashboardResponse.builder()

                .totalBookings(
                        bookingRepository
                                .countByCustomerId(customerId)
                )

                .pendingBookings(
                        bookingRepository
                                .countByCustomerIdAndStatus(
                                        customerId,
                                        BookingStatus.PENDING
                                )
                )

                .confirmedBookings(
                        bookingRepository
                                .countByCustomerIdAndStatus(
                                        customerId,
                                        BookingStatus.CONFIRMED
                                )
                )

                .providerOnTheWayBookings(
                        bookingRepository
                                .countByCustomerIdAndStatus(
                                        customerId,
                                        BookingStatus.PROVIDER_ON_THE_WAY
                                )
                )

                .inProgressBookings(
                        bookingRepository
                                .countByCustomerIdAndStatus(
                                        customerId,
                                        BookingStatus.IN_PROGRESS
                                )
                )

                .completedBookings(
                        bookingRepository
                                .countByCustomerIdAndStatus(
                                        customerId,
                                        BookingStatus.COMPLETED
                                )
                )

                .rejectedBookings(
                        bookingRepository
                                .countByCustomerIdAndStatus(
                                        customerId,
                                        BookingStatus.REJECTED
                                )
                )

                .cancelledByCustomerBookings(
                        bookingRepository
                                .countByCustomerIdAndStatus(
                                        customerId,
                                        BookingStatus.CANCELLED_BY_CUSTOMER
                                )
                )

                .cancelledByProviderBookings(
                        bookingRepository
                                .countByCustomerIdAndStatus(
                                        customerId,
                                        BookingStatus.CANCELLED_BY_PROVIDER
                                )
                )

                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDashboardResponse getProviderDashboard() {

        Provider provider =
                getAuthenticatedProvider();

        UUID providerId =
                provider.getId();

        return BookingDashboardResponse.builder()

                .totalBookings(
                        bookingRepository
                                .countByProviderId(providerId)
                )

                .pendingBookings(
                        bookingRepository
                                .countByProviderIdAndStatus(
                                        providerId,
                                        BookingStatus.PENDING
                                )
                )

                .confirmedBookings(
                        bookingRepository
                                .countByProviderIdAndStatus(
                                        providerId,
                                        BookingStatus.CONFIRMED
                                )
                )

                .providerOnTheWayBookings(
                        bookingRepository
                                .countByProviderIdAndStatus(
                                        providerId,
                                        BookingStatus.PROVIDER_ON_THE_WAY
                                )
                )

                .inProgressBookings(
                        bookingRepository
                                .countByProviderIdAndStatus(
                                        providerId,
                                        BookingStatus.IN_PROGRESS
                                )
                )

                .completedBookings(
                        bookingRepository
                                .countByProviderIdAndStatus(
                                        providerId,
                                        BookingStatus.COMPLETED
                                )
                )

                .rejectedBookings(
                        bookingRepository
                                .countByProviderIdAndStatus(
                                        providerId,
                                        BookingStatus.REJECTED
                                )
                )

                .cancelledByCustomerBookings(
                        bookingRepository
                                .countByProviderIdAndStatus(
                                        providerId,
                                        BookingStatus.CANCELLED_BY_CUSTOMER
                                )
                )

                .cancelledByProviderBookings(
                        bookingRepository
                                .countByProviderIdAndStatus(
                                        providerId,
                                        BookingStatus.CANCELLED_BY_PROVIDER
                                )
                )

                .build();
    }

    private BookingStatusHistoryResponse mapStatusHistory(
            BookingStatusHistory history) {

        User changedBy =
                history.getChangedBy();

        String name =
                changedBy.getFirstName()
                        + " "
                        + changedBy.getLastName();

        return new BookingStatusHistoryResponse(
                history.getId(),
                history.getOldStatus(),
                history.getNewStatus(),
                changedBy.getId(),
                name,
                history.getChangedAt(),
                history.getReason()
        );
    }


    private void saveStatusHistory(
            Booking booking,
            BookingStatus oldStatus,
            BookingStatus newStatus,
            User changedBy,
            String reason) {

        BookingStatusHistory history =
                BookingStatusHistory.builder()
                        .booking(booking)
                        .oldStatus(oldStatus)
                        .newStatus(newStatus)
                        .changedBy(changedBy)
                        .changedAt(LocalDateTime.now())
                        .reason(reason)
                        .build();

        bookingStatusHistoryRepository.save(history);
    }

    private PageResponse<BookingResponse> mapToPageResponse(
            Page<Booking> page) {

        List<BookingResponse> content =
                page.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return PageResponse.<BookingResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    private Provider getAuthenticatedProvider() {

        User user = getAuthenticatedUser();

        return providerRepository
                .findByUserIdAndActiveTrue(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active provider profile not found."
                        ));
    }


    private void sendBookingStatusNotification(
            Booking booking,
            BookingStatus status) {

        UUID customerId =
                booking.getCustomer().getId();

        UUID bookingId =
                booking.getId();

        switch (status) {

            case CONFIRMED ->

                    notificationService.createNotification(
                            customerId,
                            NotificationType.BOOKING_CONFIRMED,
                            "Booking Confirmed",
                            "Your booking has been confirmed by the provider.",
                            bookingId
                    );

            case REJECTED ->

                    notificationService.createNotification(
                            customerId,
                            NotificationType.BOOKING_REJECTED,
                            "Booking Rejected",
                            "Unfortunately, your booking was rejected.",
                            bookingId
                    );

            case PROVIDER_ON_THE_WAY ->

                    notificationService.createNotification(
                            customerId,
                            NotificationType.PROVIDER_ON_THE_WAY,
                            "Provider On The Way",
                            "Your service provider is on the way.",
                            bookingId
                    );

            case IN_PROGRESS ->

                    notificationService.createNotification(
                            customerId,
                            NotificationType.BOOKING_STARTED,
                            "Service Started",
                            "Your service has started.",
                            bookingId
                    );

            case COMPLETED ->

                    notificationService.createNotification(
                            customerId,
                            NotificationType.BOOKING_COMPLETED,
                            "Booking Completed",
                            "Your booking has been completed successfully.",
                            bookingId
                    );

            case CANCELLED_BY_CUSTOMER,
                 CANCELLED_BY_PROVIDER ->

                    sendCancellationNotification(booking);

            default -> {
                // No notification required
            }
        }
    }

    private void sendCancellationNotification(
            Booking booking) {

        UUID customerId =
                booking.getCustomer().getId();

        UUID providerUserId =
                booking.getProvider()
                        .getUser()
                        .getId();

        String message =
                booking.getStatus()
                        == BookingStatus.CANCELLED_BY_CUSTOMER
                        ? "The customer cancelled the booking."
                        : "The provider cancelled the booking.";

        notificationService.createNotification(
                customerId,
                NotificationType.BOOKING_CANCELLED,
                "Booking Cancelled",
                message,
                booking.getId()
        );

        notificationService.createNotification(
                providerUserId,
                NotificationType.BOOKING_CANCELLED,
                "Booking Cancelled",
                message,
                booking.getId()
        );
    }
}
