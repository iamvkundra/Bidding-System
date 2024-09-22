package com.intuit.auction.service.notifications.dto;

import java.time.LocalDateTime;

import com.intuit.auction.service.enums.NotificationType;
import lombok.Data;

@Data
public class NotificationRequest {
    private String recipientEmail;
    private String subject;
    private String content;
    private int retryCount = 0;
    private NotificationType notificationType;

    public void incrementRetryCount() {
        this.retryCount++;
    }
}
