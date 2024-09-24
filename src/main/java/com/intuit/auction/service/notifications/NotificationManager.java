package com.intuit.auction.service.notifications;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.intuit.auction.service.enums.NotificationType;
import com.intuit.auction.service.notifications.dto.NotificationRequest;
import com.intuit.auction.service.notifications.impl.EmailNotificationService;
import com.intuit.auction.service.notifications.impl.SmsNotificationService;
import com.intuit.auction.service.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class NotificationManager {

    public static final int MAX_RETRY_ATTEMPTS = 3;

    private static final Logger log = LoggerFactory.getLogger(NotificationManager.class);
    private final Map<NotificationType, NotificationService> notificationServices;
    public final ConcurrentLinkedQueue<NotificationRequest> notificationQueue = new ConcurrentLinkedQueue<>();

    public NotificationManager() {
        notificationServices = new HashMap<>();
        notificationServices.put(NotificationType.EMAIL, new EmailNotificationService());
        notificationServices.put(NotificationType.SMS, new SmsNotificationService());
    }

    public void queueNotification(NotificationRequest request) {
        this.notificationQueue.offer(request);
        log.info("Notification queued for {}", CommonUtils.toJson(request, false));
    }

    @Scheduled(fixedRate = 60000)
    public void processNotificationQueue() {
        log.info("LocalDateTime: " + LocalDateTime.now() + " Processing notification queue. Current size: {}", notificationQueue.size());
        LocalDateTime now = LocalDateTime.now();
        NotificationRequest request;
        while (!notificationQueue.isEmpty()) {
            request = notificationQueue.poll();
            NotificationService service = notificationServices.get(request.getNotificationType());
            if (service != null) {
                try {
                    service.sendNotification(request);
                    log.info("Notification sent successfully: {} to {}", request.getNotificationType(), request.getRecipientEmail());
                } catch (Exception e) {
                    log.error("Failed to send notification: {} to {}", request.getNotificationType(), request.getRecipientEmail(), e);
                    handleFailedNotification(request);
                }
            } else {
                log.error("No service found for notification type: {}", request.getNotificationType());
            }
        }
    }

    private void handleFailedNotification(NotificationRequest request) {
        if (request.getRetryCount() < MAX_RETRY_ATTEMPTS) {
            request.incrementRetryCount();
            notificationQueue.offer(request);
            log.info("Added failed notification to retry queue. Recipient: {}, Type: {}, Retry count: {}",
                    request.getRecipientEmail(), request.getNotificationType(), request.getRetryCount());
        } else {
            log.error("Max retry attempts reached for notification to {}. Notification abandoned.",
                    request.getRecipientEmail());
        }
    }
}
