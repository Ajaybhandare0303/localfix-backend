package com.localfix.notification.service;

import com.localfix.notification.dto.response.NotificationResponse;
import com.localfix.notification.enums.NotificationType;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface NotificationService {

    void createNotification(
            UUID userId,
            NotificationType type,
            String title,
            String message,
            UUID referenceId
    );

    Page<NotificationResponse> getMyNotifications(
            int page,
            int size
    );

    long getUnreadCount();

    void markAsRead(UUID notificationId);
}