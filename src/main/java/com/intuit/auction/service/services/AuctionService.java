package com.intuit.auction.service.services;

import com.intuit.auction.service.dto.AuctionRequest;

public interface AuctionService {
    void createAuction(AuctionRequest auctionRequest) throws Exception;
}
