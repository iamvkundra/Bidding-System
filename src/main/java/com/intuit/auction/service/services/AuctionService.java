package com.intuit.auction.service.services;

import java.util.List;

import com.intuit.auction.service.dto.AuctionFilter;
import com.intuit.auction.service.dto.AuctionRequest;
import com.intuit.auction.service.dto.AuctionResponseDto;
import com.intuit.auction.service.entity.Auction;
import org.springframework.http.HttpStatusCode;

public interface AuctionService {

    void createAuction(String username, AuctionRequest auctionRequest) throws Exception;
    void closeAuction(String vendorUsername, String auctionId) throws Exception;
    AuctionResponseDto getAuctionById(String auctionId);
    List<AuctionResponseDto> searchAuction(AuctionFilter filter);

    Auction getAuctionDetails(String auctionId);
    void registerUserForAuction(String username, String auctionId) throws Exception;
    boolean isUserRegisteredForAuction(String customerId, String auctionId) throws Exception;

    List<AuctionResponseDto> getAuctionsOfUser(String name);
}
