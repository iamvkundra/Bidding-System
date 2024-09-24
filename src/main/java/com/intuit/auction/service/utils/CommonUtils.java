package com.intuit.auction.service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.intuit.auction.service.dto.AuctionResponseDto;
import com.intuit.auction.service.entity.Auction;
import com.intuit.auction.service.enums.NotificationType;
import com.intuit.auction.service.notifications.dto.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

@Slf4j
public class CommonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static String toJson(Object body, Boolean prettyPrint) {
        try {
            if (ObjectUtils.isNotEmpty(body) && body instanceof String) {
                return (String) body;
            }
            if (Boolean.TRUE.equals(prettyPrint)) {
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
            } else {
                return objectMapper.writeValueAsString(body);
            }
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static NotificationRequest getNotificationRequest(Auction auction) {
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setNotificationType(NotificationType.EMAIL);
        notificationRequest.setRecipientEmail(auction.getWinner().getUser().getEmail());
        notificationRequest.setSubject("You have successfully won the auction: " + auction.getAuctionId());
        notificationRequest.setContent("Please contact vendor with given auctionId and product: " + auction.getProduct().getProductName());
        return notificationRequest;
    }
}
