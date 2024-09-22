package com.intuit.auction.service.notifications;

import com.intuit.auction.service.notifications.dto.NotificationRequest;

public interface NotificationService {
    void sendNotification(NotificationRequest request);
}
