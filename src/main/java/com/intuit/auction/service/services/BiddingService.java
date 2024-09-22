package com.intuit.auction.service.services;

import java.util.List;

import com.intuit.auction.service.dto.BidRequest;
import com.intuit.auction.service.dto.BidResponse;
import com.intuit.auction.service.entity.Bid;

public interface BiddingService {
    void placeBid(String username, BidRequest bidRequest) throws Exception;
    List<BidResponse> getAuctionBids(String auctionId);
    List<BidResponse> getBidsByAuctionIdAndCustomerId(String customerUsername, String auctionId);
    List<BidResponse> getBidsByUsername(String customerUsername);
}
