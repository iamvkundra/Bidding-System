package com.intuit.auction.service.services;

import java.util.List;

import com.intuit.auction.service.dto.AuctionFilter;
import com.intuit.auction.service.dto.AuctionRegistrationDto;
import com.intuit.auction.service.dto.AuctionRequest;
import com.intuit.auction.service.entity.Auction;

public interface AuctionService {

    void createAuction(AuctionRequest auctionRequest) throws Exception;
    void closeAuction(String vendorUsername, String auctionId) throws Exception;

    List<Auction> searchAuction(AuctionFilter filter);

    Auction getAuctionDetails(String auctionId);
    void registerUserForAuction(AuctionRegistrationDto auctionRegistration) throws Exception;
    boolean isUserRegisteredForAuction(String customerId, String auctionId) throws Exception;
}
