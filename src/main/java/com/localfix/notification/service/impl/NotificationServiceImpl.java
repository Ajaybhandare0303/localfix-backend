package com.localfix.notification.service.impl;

import com.localfix.booking.dto.response.BookingStatusHistoryResponse;
import com.localfix.booking.entity.BookingStatusHistory;
import com.localfix.common.exception.ResourceNotFoundException;
import com.localfix.notification.dto.response.NotificationResponse;
import com.localfix.notification.entity.Notification;
import com.localfix.notification.enums.NotificationType;
import com.localfix.notification.repository.NotificationRepository;
import com.localfix.notification.service.NotificationService;
import com.localfix.user.entity.User;
import com.localfix.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void createNotification(
            UUID userId,
            NotificationType type,
            String title,
            String message,
            UUID referenceId) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found."
                                ));

        Notification notification =
                Notification.builder()
                        .user(user)
                        .type(type)
                        .title(title)
                        .message(message)
                        .referenceId(referenceId)
                        .read(false)
                        .createdAt(LocalDateTime.now())
                        .build();

        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyNotifications(
            int page,
            int size) {

        User user = getAuthenticatedUser();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.DESC,
                                "createdAt"
                        )
                );

        return notificationRepository
                .findAllByUserIdOrderByCreatedAtDesc(
                        user.getId(),
                        pageable
                )
                .map(this::mapToResponse);
    }

    private NotificationResponse mapToResponse(
            Notification notifications) {

        return NotificationResponse.builder()
                .id(notifications.getId())
                .type(notifications.getType())
                .message(notifications.getMessage())
                .referenceId(notifications.getReferenceId())
                .read(notifications.isRead())
                .title(notifications.getTitle())
                .createdAt(notifications.getCreatedAt()).build();
    }

    @Override
    public long getUnreadCount() {
        return 0;
    }

    @Override
    @Transactional
    public void markAsRead(UUID notificationId) {

        User user =
                getAuthenticatedUser();

        Notification notification =
                notificationRepository
                        .findById(notificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found."
                                ));

        if (!notification.getUser()
                .getId()
                .equals(user.getId())) {

            throw new AccessDeniedException(
                    "You are not authorized to update this notification."
            );
        }

        notification.setRead(true);

        notificationRepository.save(notification);
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
}