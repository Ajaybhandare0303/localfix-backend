package com.localfix.notification.dto.response;

import com.localfix.notification.enums.NotificationType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record NotificationResponse(

        UUID id,

        NotificationType type,

        String title,

        String message,

        UUID referenceId,

        boolean read,

        LocalDateTime createdAt

) {
}