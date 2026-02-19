package com.scooter.app.modules.notifications.dto;

import com.scooter.app.modules.notifications.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class NotificationResponse {
    private UUID id;
    private String message;
    private NotificationType type;
    private Boolean read;
    private LocalDateTime createdAt;
}
