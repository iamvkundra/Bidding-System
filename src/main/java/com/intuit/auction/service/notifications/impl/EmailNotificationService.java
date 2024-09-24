package com.intuit.auction.service.notifications.impl;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.intuit.auction.service.notifications.NotificationService;
import com.intuit.auction.service.notifications.dto.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class EmailNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    @Override
    public void sendNotification(NotificationRequest request) {
        log.info("Sending email notification to: {}", request.getRecipientEmail());
    }
}
