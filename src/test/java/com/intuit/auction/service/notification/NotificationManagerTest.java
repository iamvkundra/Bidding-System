package com.intuit.auction.service.notification;

import com.intuit.auction.service.enums.NotificationType;
import com.intuit.auction.service.notifications.NotificationManager;
import com.intuit.auction.service.notifications.dto.NotificationRequest;
import com.intuit.auction.service.notifications.impl.EmailNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationManagerTest {

    private NotificationManager notificationManager;

    @Mock
    private EmailNotificationService emailNotificationService;


    @BeforeEach
    void setup() {
        notificationManager = new NotificationManager();
    }

    @Test
    void shouldQueueNotification() {
        NotificationRequest request = new NotificationRequest();
        request.setRecipientEmail("test@example.com");
        request.setNotificationType(NotificationType.EMAIL);

        notificationManager.queueNotification(request);

        assert !notificationManager.notificationQueue.isEmpty();
    }

    @Test
    void shouldProcessNotificationQueueAndSendNotification() {
        NotificationRequest request = new NotificationRequest();
        request.setRecipientEmail("test@example.com");
        request.setNotificationType(NotificationType.EMAIL);

        notificationManager.queueNotification(request);
        notificationManager.processNotificationQueue();

        assert notificationManager.notificationQueue.isEmpty();
    }
    

    @Test
    void shouldAbandonNotificationAfterMaxRetryAttempts() {
        NotificationRequest request = new NotificationRequest();
        request.setRecipientEmail("test@example.com");
        request.setNotificationType(NotificationType.EMAIL);

        for (int i = 0; i < NotificationManager.MAX_RETRY_ATTEMPTS; i++) {
            notificationManager.queueNotification(request);
            notificationManager.processNotificationQueue();
        }
        assert notificationManager.notificationQueue.isEmpty();

    }
}