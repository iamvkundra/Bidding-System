package com.intuit.auction.service.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BidResponse {
    private String bidId;
    private double bidAmount;
    private String username;
    private LocalDateTime timeOfBid;
}
