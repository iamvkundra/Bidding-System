package com.intuit.auction.service.services;

import java.util.List;

import com.intuit.auction.service.dto.BidRequest;
import com.intuit.auction.service.entity.Bid;

public interface BiddingService {
    void placeBid(BidRequest bidRequest) throws Exception;
    List<Bid> getAuctionBids(String auctionId);
    List<Bid> getBidsByAuctionIdAndCustomerId(String customerUsername, String auctionId);
    List<Bid> getBidsByUsername(String customerUsername);
}
