package com.localfix.booking.entity;

import com.localfix.booking.enums.BookingStatus;
import com.localfix.provider.entity.Provider;
import com.localfix.service.entity.Service;
import com.localfix.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(
        name = "bookings",
        indexes = {
                @Index(
                        name = "idx_booking_customer",
                        columnList = "customer_id"
                ),
                @Index(
                        name = "idx_booking_provider",
                        columnList = "provider_id"
                ),
                @Index(
                        name = "idx_booking_service",
                        columnList = "service_id"
                ),
                @Index(
                        name = "idx_booking_date",
                        columnList = "booking_date"
                ),
                @Index(
                        name = "idx_booking_status",
                        columnList = "status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "customer_id",
            nullable = false
    )
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "provider_id",
            nullable = false
    )
    private Provider provider;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "service_id",
            nullable = false
    )
    private Service service;

    @Column(
            name = "booking_date",
            nullable = false
    )
    private LocalDate bookingDate;

    @Column(
            name = "booking_time",
            nullable = false
    )
    private LocalTime bookingTime;

    @Column(
            name = "address",
            nullable = false,
            length = 500
    )
    private String address;

    @Column(
            name = "city",
            nullable = false,
            length = 100
    )
    private String city;

    @Column(
            name = "state",
            nullable = false,
            length = 100
    )
    private String state;

    @Column(
            name = "pincode",
            length = 10
    )
    private String pincode;

    @Column(
            name = "customer_note",
            length = 1000
    )
    private String customerNote;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 40
    )
    @Builder.Default
    private BookingStatus status =
            BookingStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now =
                LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {

        updatedAt =
                LocalDateTime.now();
    }
}