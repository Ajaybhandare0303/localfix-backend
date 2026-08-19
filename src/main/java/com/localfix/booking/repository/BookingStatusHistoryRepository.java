package com.localfix.booking.repository;

import com.localfix.booking.entity.BookingStatusHistory;
import com.localfix.booking.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface BookingStatusHistoryRepository
        extends JpaRepository<BookingStatusHistory, UUID> {

    List<BookingStatusHistory>
    findAllByBookingIdOrderByChangedAtAsc(UUID bookingId);



}