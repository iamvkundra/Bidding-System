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

    @Value("${email.sender}")
    private String sender;

    @Value("${email.host}")
    private String host;

    @Override
    public void sendNotification(NotificationRequest request) {
        log.info("Sending email notification to: {}", request.getRecipientEmail());

        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);

        Session session = Session.getDefaultInstance(properties);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(request.getRecipientEmail()));
            message.setSubject(request.getSubject());
            message.setText(request.getContent());

            Transport.send(message);
            log.info("Email successfully sent to {}", request.getRecipientEmail());
        } catch (MessagingException mex) {
            log.error("Failed to send email to {}", request.getRecipientEmail(), mex);
            throw new RuntimeException("Failed to send email", mex);
        }
    }
}
