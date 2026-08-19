package com.localfix.review.service.impl;

import com.localfix.booking.entity.Booking;
import com.localfix.booking.enums.BookingStatus;
import com.localfix.booking.repository.BookingRepository;
import com.localfix.common.exception.ResourceAlreadyExistsException;
import com.localfix.common.exception.ResourceNotFoundException;
import com.localfix.provider.entity.Provider;
import com.localfix.provider.repository.ProviderRepository;
import com.localfix.review.dto.request.CreateReviewRequest;
import com.localfix.review.dto.request.UpdateReviewRequest;
import com.localfix.review.dto.response.ProviderRatingResponse;
import com.localfix.review.dto.response.ReviewResponse;
import com.localfix.review.entity.Review;
import com.localfix.review.repository.ReviewRepository;
import com.localfix.review.service.ReviewService;
import com.localfix.servicecategory.dto.response.PageResponse;
import com.localfix.user.entity.User;
import com.localfix.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl
        implements ReviewService {

    private final ReviewRepository reviewRepository;

    private final ProviderRepository providerRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;


    @Override
    @Transactional
    public ReviewResponse createReview(
            CreateReviewRequest request) {

        User customer = getAuthenticatedUser();

        Booking booking=
                bookingRepository.findById(request.bookingId())
                        .orElseThrow(()->
                                new ResourceNotFoundException("Booking not found")
                        );
        if (!booking.getCustomer()
                .getId()
                .equals(customer.getId())) {

            throw new AccessDeniedException(
                    "You are not authorized to review this booking."
            );
        }

        if (booking.getStatus()
                != BookingStatus.COMPLETED) {

            throw new IllegalStateException(
                    "Review can only be submitted for completed bookings."
            );
        }

        if (reviewRepository
                .existsByBookingId(
                        booking.getId()
                )) {

            throw new ResourceAlreadyExistsException(
                    "This booking has already been reviewed."
            );
        }

        Provider provider =
                booking.getProvider();

        Review review =
                Review.builder()
                        .booking(booking)
                        .customer(customer)
                        .provider(provider)
                        .rating(request.rating())
                        .comment(request.comment())
                        .build();

        Review savedReview =
                reviewRepository.save(review);

        return mapToResponse(savedReview);

    }


    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getProviderReviews(
            UUID providerId,
            int page,
            int size) {

        providerRepository
                .findById(providerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Provider not found."
                        ));

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.DESC,
                                "createdAt"
                        )
                );

        return reviewRepository
                .findAllByProviderIdOrderByCreatedAtDesc(
                        providerId,
                        pageable
                )
                .map(this::mapToResponse);
    }


    @Override
    @Transactional(readOnly = true)
    public ProviderRatingResponse getProviderRating(
            UUID providerId) {

        providerRepository
                .findById(providerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Provider not found."
                        ));

        Double average =
                reviewRepository
                        .findAverageRating(providerId);

        long total =
                reviewRepository
                        .countByProviderId(providerId);

        long five =
                reviewRepository
                        .countByProviderIdAndRating(
                                providerId,
                                5
                        );

        long four =
                reviewRepository
                        .countByProviderIdAndRating(
                                providerId,
                                4
                        );

        long three =
                reviewRepository
                        .countByProviderIdAndRating(
                                providerId,
                                3
                        );

        long two =
                reviewRepository
                        .countByProviderIdAndRating(
                                providerId,
                                2
                        );

        long one =
                reviewRepository
                        .countByProviderIdAndRating(
                                providerId,
                                1
                        );

        return ProviderRatingResponse.builder()

                .averageRating(
                        average == null
                                ? 0.0
                                : Math.round(
                                average * 10.0
                        ) / 10.0
                )

                .totalReviews(total)

                .fiveStarReviews(five)
                .fourStarReviews(four)
                .threeStarReviews(three)
                .twoStarReviews(two)
                .oneStarReviews(one)

                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getMyReviews(
            int page,
            int size) {

        User customer =
                getAuthenticatedUser();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.DESC,
                                "createdAt"
                        )
                );

        return reviewRepository
                .findAllByCustomerIdOrderByCreatedAtDesc(
                        customer.getId(),
                        pageable
                )
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(
            UUID reviewId) {

        Review review =
                reviewRepository.findById(reviewId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Review not found."
                                ));

        return mapToResponse(review);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(
            UUID reviewId,
            UpdateReviewRequest request) {

        User customer =
                getAuthenticatedUser();

        Review review =
                reviewRepository.findById(reviewId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Review not found."
                                ));

        if (!review.getCustomer()
                .getId()
                .equals(customer.getId())) {

            throw new AccessDeniedException(
                    "You are not authorized to update this review."
            );
        }

        review.setRating(request.rating());
        review.setComment(request.comment());

        Review updatedReview =
                reviewRepository.save(review);

        return mapToResponse(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(
            UUID reviewId) {

        User customer =
                getAuthenticatedUser();

        Review review =
                reviewRepository.findById(reviewId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Review not found."
                                ));

        if (!review.getCustomer()
                .getId()
                .equals(customer.getId())) {

            throw new AccessDeniedException(
                    "You are not authorized to delete this review."
            );
        }

        reviewRepository.delete(review);
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


    private ReviewResponse mapToResponse(
            Review review) {

        return ReviewResponse.builder()
                .id(review.getId())
                .bookingId(
                        review.getBooking().getId()
                )
                .customerId(
                        review.getCustomer().getId()
                )
                .providerId(
                        review.getProvider().getId()
                )
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }


    private PageResponse<ReviewResponse>
    mapToPageResponse(
            Page<Review> page) {

        List<ReviewResponse> content =
                page.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return PageResponse
                .<ReviewResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(
                        page.getTotalElements()
                )
                .totalPages(
                        page.getTotalPages()
                )
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}