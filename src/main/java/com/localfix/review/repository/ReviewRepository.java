package com.localfix.review.repository;

import com.localfix.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository
        extends JpaRepository<Review, UUID> {

    Page<Review> findAllByProviderId(
            UUID providerId,
            Pageable pageable
    );

    Page<Review> findAllByCustomerId(
            UUID customerId,
            Pageable pageable
    );

    @Query("""
            SELECT COALESCE(AVG(r.rating), 0)
            FROM Review r
            WHERE r.provider.id = :providerId
            """)
    Double findAverageRating(
            @Param("providerId") UUID providerId
    );

    long countByProviderId(UUID providerId);

    boolean existsByBookingId(UUID bookingId);

    Optional<Review> findByBookingId(UUID bookingId);

    boolean existsByProviderIdAndCustomerId(UUID providerId,UUID customerId);

    Page<Review> findAllByCustomerIdOrderByCreatedAtDesc(
            UUID customerId,
            Pageable pageable
    );

    Page<Review> findAllByProviderIdOrderByCreatedAtDesc(
            UUID providerId,
            Pageable pageable
    );

    long countByProviderIdAndRating(
            UUID providerId,
            Integer rating
    );
}