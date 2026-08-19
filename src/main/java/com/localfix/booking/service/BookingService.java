package com.localfix.booking.service;

import com.localfix.booking.dto.request.CreateBookingRequest;
import com.localfix.booking.dto.request.RescheduleBookingRequest;
import com.localfix.booking.dto.request.UpdateBookingStatusRequest;
import com.localfix.booking.dto.response.BookingDashboardResponse;
import com.localfix.booking.dto.response.BookingResponse;
import com.localfix.booking.dto.response.BookingStatusHistoryResponse;
import com.localfix.booking.enums.BookingStatus;
import com.localfix.servicecategory.dto.response.PageResponse;

import java.util.List;
import java.util.UUID;

public interface BookingService {

    BookingResponse createBooking(
            CreateBookingRequest request
    );

    BookingResponse getBookingById(
            UUID bookingId
    );

    PageResponse<BookingResponse> getMyBookings(
            int page,
            int size
    );

    PageResponse<BookingResponse> getProviderBookings(
            int page,
            int size
    );

    BookingResponse updateBookingStatus(
            UUID bookingId,
            UpdateBookingStatusRequest request
    );

    BookingResponse cancelBooking(
            UUID bookingId
    );

    List<BookingStatusHistoryResponse> getBookingStatusHistory(UUID bookingId);

    BookingResponse rescheduleBooking(
            UUID bookingId,
            RescheduleBookingRequest request
    );

    PageResponse<BookingResponse> getMyBookings(
            BookingStatus status,
            int page,
            int size
    );

    PageResponse<BookingResponse> getProviderBookings(
            BookingStatus status,
            int page,
            int size
    );

    BookingDashboardResponse getMyDashboard();

    BookingDashboardResponse getProviderDashboard();




}