package com.localfix.booking.repository;

import com.localfix.booking.entity.Booking;
import com.localfix.booking.enums.BookingStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface BookingRepository
        extends JpaRepository<Booking, UUID> {

    Page<Booking> findAllByCustomerId(
            UUID customerId,
            Pageable pageable
    );

    Page<Booking> findAllByProviderId(
            UUID providerId,
            Pageable pageable
    );

    Page<Booking> findAllByProviderIdAndStatus(
            UUID providerId,
            BookingStatus status,
            Pageable pageable
    );

    boolean existsByProviderIdAndBookingDateAndBookingTimeAndStatusIn(
            UUID providerId,
            LocalDate bookingDate,
            LocalTime bookingTime,
            java.util.List<BookingStatus> statuses
    );
    boolean existsByProviderIdAndBookingDateAndBookingTimeAndStatusInAndIdNot(
            UUID providerId,
            LocalDate bookingDate,
            LocalTime bookingTime,
            List<BookingStatus> statuses,
            UUID bookingId
    );

    Page<Booking> findAllByCustomerIdOrderByBookingDateDescBookingTimeDesc(
            UUID customerId,
            Pageable pageable
    );

    Page<Booking> findAllByCustomerIdAndStatusOrderByBookingDateDescBookingTimeDesc(
            UUID customerId,
            BookingStatus status,
            Pageable pageable
    );

    Page<Booking> findAllByProviderIdOrderByBookingDateDescBookingTimeDesc(
            UUID providerId,
            Pageable pageable
    );

    Page<Booking> findAllByProviderIdAndStatusOrderByBookingDateDescBookingTimeDesc(
            UUID providerId,
            BookingStatus status,
            Pageable pageable
    );

    long countByCustomerId(UUID customerId);

    long countByCustomerIdAndStatus(
            UUID customerId,
            BookingStatus status
    );

    long countByProviderId(UUID providerId);

    long countByProviderIdAndStatus(
            UUID providerId,
            BookingStatus status
    );

}