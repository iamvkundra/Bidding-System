package com.intuit.auction.service.services;

import java.util.List;

import com.intuit.auction.service.dto.BidRequest;
import com.intuit.auction.service.dto.BidResponse;
import com.intuit.auction.service.entity.Bid;
import com.intuit.auction.service.exceptions.BiddingException;

public interface BiddingService {
    void placeBid(String username, BidRequest bidRequest) throws Exception;
    List<BidResponse> getAuctionBids(String auctionId) throws BiddingException;
    List<BidResponse> getBidsByAuctionIdAndCustomerId(String customerUsername, String auctionId);
    List<BidResponse> getBidsByUsername(String customerUsername);

    BidResponse getWinningBid(String auctionId) throws BiddingException;
}
