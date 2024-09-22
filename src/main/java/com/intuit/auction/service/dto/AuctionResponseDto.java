package com.intuit.auction.service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.intuit.auction.service.entity.Bid;
import com.intuit.auction.service.entity.Product;
import com.intuit.auction.service.enums.AuctionStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuctionResponseDto {
    private String auctionId;
    private String vendorUsername;
    private Product product;
    private AuctionStatus auctionStatus;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double auctionBasePrice;
    private double highestBid;
}
