package com.intuit.auction.service.dto;

import java.time.LocalDateTime;

import com.intuit.auction.service.entity.Product;
import lombok.Data;

@Data
public class AuctionRequest {
    private String vendorUsername;
    private Product product;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double auctionBasePrice;
}
