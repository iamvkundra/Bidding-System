package com.intuit.auction.service.notifications.impl;

import com.intuit.auction.service.notifications.NotificationService;
import com.intuit.auction.service.notifications.dto.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmsNotificationService implements NotificationService {
    private static final Logger log = LoggerFactory.getLogger(SmsNotificationService.class);

    @Override
    public void sendNotification(NotificationRequest request) {
        log.info("SMS NOTIFICATION TRIGGERED: {}", request);
    }
}
