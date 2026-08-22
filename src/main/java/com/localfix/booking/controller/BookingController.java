package com.localfix.booking.controller;

import com.localfix.booking.dto.request.CreateBookingRequest;
import com.localfix.booking.dto.request.RescheduleBookingRequest;
import com.localfix.booking.dto.request.UpdateBookingStatusRequest;
import com.localfix.booking.dto.response.BookingDashboardResponse;
import com.localfix.booking.dto.response.BookingResponse;
import com.localfix.booking.dto.response.BookingStatusHistoryResponse;
import com.localfix.booking.enums.BookingStatus;
import com.localfix.booking.service.BookingService;
import com.localfix.common.response.ApiResponse;

import com.localfix.servicecategory.dto.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;


    @Operation(
            summary = "Create booking",
            description = "Creates a new service booking for the authenticated customer."
    )
    @PostMapping
    public ResponseEntity<
            ApiResponse<BookingResponse>>
    createBooking(
            @Valid
            @RequestBody
            CreateBookingRequest request) {

        BookingResponse response =
                bookingService.createBooking(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse
                                .<BookingResponse>builder()
                                .success(true)
                                .message(
                                        "Booking created successfully."
                                )
                                .data(response)
                                .build()
                );
    }

/*
    @GetMapping("/{bookingId}")
    public ResponseEntity<
            ApiResponse<BookingResponse>>
    getBooking(
            @PathVariable UUID bookingId) {

        BookingResponse response =
                bookingService.getBookingById(
                        bookingId
                );

        return ResponseEntity.ok(
                ApiResponse
                        .<BookingResponse>builder()
                        .success(true)
                        .message(
                                "Booking fetched successfully."
                        )
                        .data(response)
                        .build()
        );
    }*/


/*
    @GetMapping("/my")
    public ResponseEntity<
            ApiResponse<PageResponse<BookingResponse>>>
    getMyBookings(

            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(100)
            int size) {

        PageResponse<BookingResponse> response =
                bookingService.getMyBookings(
                        page,
                        size
                );

        return ResponseEntity.ok(
                ApiResponse
                        .<PageResponse<BookingResponse>>builder()
                        .success(true)
                        .message(
                                "Bookings fetched successfully."
                        )
                        .data(response)
                        .build()
        );
    }
*/


    @GetMapping("/provider/my")
    public ResponseEntity<
            ApiResponse<PageResponse<BookingResponse>>>
    getProviderBookings(

            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(100)
            int size) {

        PageResponse<BookingResponse> response =
                bookingService.getProviderBookings(
                        page,
                        size
                );

        return ResponseEntity.ok(
                ApiResponse
                        .<PageResponse<BookingResponse>>builder()
                        .success(true)
                        .message(
                                "Provider bookings fetched successfully."
                        )
                        .data(response)
                        .build()
        );
    }

    @PatchMapping("/{bookingId}/status")
    public ResponseEntity<
            ApiResponse<BookingResponse>>
    updateBookingStatus(

            @PathVariable UUID bookingId,

            @Valid
            @RequestBody
            UpdateBookingStatusRequest request) {

        BookingResponse response =
                bookingService.updateBookingStatus(
                        bookingId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse
                        .<BookingResponse>builder()
                        .success(true)
                        .message(
                                "Booking status updated successfully."
                        )
                        .data(response)
                        .build()
        );
    }

    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<
            ApiResponse<BookingResponse>>
    cancelBooking(
            @PathVariable UUID bookingId) {

        BookingResponse response =
                bookingService.cancelBooking(
                        bookingId
                );

        return ResponseEntity.ok(
                ApiResponse
                        .<BookingResponse>builder()
                        .success(true)
                        .message(
                                "Booking cancelled successfully."
                        )
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{bookingId}/history")
    public ResponseEntity<
            ApiResponse<List<BookingStatusHistoryResponse>>>
    getBookingStatusHistory(
            @PathVariable UUID bookingId) {

        List<BookingStatusHistoryResponse> history =
                bookingService.getBookingStatusHistory(
                        bookingId
                );

        return ResponseEntity.ok(
                ApiResponse
                        .<List<BookingStatusHistoryResponse>>builder()
                        .success(true)
                        .message(
                                "Booking status history retrieved successfully."
                        )
                        .data(history)
                        .build()
        );
    }

    @PatchMapping("/{bookingId}/reschedule")
    public ResponseEntity<
            ApiResponse<BookingResponse>>
    rescheduleBooking(

            @PathVariable UUID bookingId,

            @Valid
            @RequestBody
            RescheduleBookingRequest request) {

        BookingResponse response =
                bookingService.rescheduleBooking(
                        bookingId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse
                        .<BookingResponse>builder()
                        .success(true)
                        .message(
                                "Booking rescheduled successfully."
                        )
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/my")
    public ResponseEntity<
            ApiResponse<PageResponse<BookingResponse>>>
    getMyBookings(

            @RequestParam(required = false)
            BookingStatus status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        PageResponse<BookingResponse> response =
                bookingService.getMyBookings(
                        status,
                        page,
                        size
                );

        return ResponseEntity.ok(
                ApiResponse
                        .<PageResponse<BookingResponse>>builder()
                        .success(true)
                        .message(
                                "My bookings retrieved successfully."
                        )
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/provider")
    public ResponseEntity<
            ApiResponse<PageResponse<BookingResponse>>>
    getProviderBookings(

            @RequestParam(required = false)
            BookingStatus status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        PageResponse<BookingResponse> response =
                bookingService.getProviderBookings(
                        status,
                        page,
                        size
                );

        return ResponseEntity.ok(
                ApiResponse
                        .<PageResponse<BookingResponse>>builder()
                        .success(true)
                        .message(
                                "Provider bookings retrieved successfully."
                        )
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<
            ApiResponse<BookingResponse>>
    getBookingById(
            @PathVariable UUID bookingId) {

        BookingResponse response =
                bookingService.getBookingById(
                        bookingId
                );

        return ResponseEntity.ok(
                ApiResponse
                        .<BookingResponse>builder()
                        .success(true)
                        .message(
                                "Booking retrieved successfully."
                        )
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/dashboard")
    public ResponseEntity<
            ApiResponse<BookingDashboardResponse>>
    getMyDashboard() {

        BookingDashboardResponse response =
                bookingService.getMyDashboard();

        return ResponseEntity.ok(
                ApiResponse
                        .<BookingDashboardResponse>builder()
                        .success(true)
                        .message(
                                "Booking dashboard retrieved successfully."
                        )
                        .data(response)
                        .build()
        );
    }


    @GetMapping("/provider/dashboard")
    public ResponseEntity<
            ApiResponse<BookingDashboardResponse>>
    getProviderDashboard() {

        BookingDashboardResponse response =
                bookingService.getProviderDashboard();

        return ResponseEntity.ok(
                ApiResponse
                        .<BookingDashboardResponse>builder()
                        .success(true)
                        .message(
                                "Provider booking dashboard retrieved successfully."
                        )
                        .data(response)
                        .build()
        );
    }
}