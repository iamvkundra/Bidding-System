package com.intuit.auction.service.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.intuit.auction.service.dto.BidRequest;
import com.intuit.auction.service.entity.Auction;
import com.intuit.auction.service.entity.Bid;
import com.intuit.auction.service.entity.account.Account;
import com.intuit.auction.service.entity.account.Customer;
import com.intuit.auction.service.entity.account.Vendor;
import com.intuit.auction.service.enums.AuctionStatus;
import com.intuit.auction.service.exceptions.BiddingException;
import com.intuit.auction.service.repositories.BidRepository;
import com.intuit.auction.service.services.AccountService;
import com.intuit.auction.service.services.AuctionService;
import com.intuit.auction.service.services.BiddingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BiddingServiceImpl implements BiddingService {

    private static final Logger log = LoggerFactory.getLogger(BiddingServiceImpl.class);
    private final BidRepository bidRepository;
    private final AccountService accountService;
    private final AuctionService auctionService;

    public BiddingServiceImpl(BidRepository bidRepository, AccountService accountService, AuctionService auctionService) {
        this.bidRepository = bidRepository;
        this.accountService = accountService;
        this.auctionService = auctionService;
    }

    @Override
    public void placeBid(BidRequest bidRequest) throws Exception {
        Account customer = accountService.getUserByUsername(bidRequest.getCustomerUsername());

        if (customer instanceof Vendor) throw new Exception("Vendor account has no bidding access");
        Auction auction = auctionService.getAuctionDetails(bidRequest.getAuctionId());

        if (!auction.getAuctionStatus().equals(AuctionStatus.ACTIVE)) {
            throw new Exception("Cannot Bid into Scheduled or Closed Bid");
        }

        if (!auctionService.isUserRegisteredForAuction(customer.getUsername(), auction.getAuctionId())) {
            throw new BiddingException("Cannot Bid, The customer need to register first");
        }

        if (auction.getAuctionBasePrice() > bidRequest.getBidAmount()) {
            throw new BiddingException("Customer Bidding amount is less than auctionBaseAmount: " + auction.getAuctionBasePrice());
        }

        Bid bid = new Bid(auction, (Customer) customer, bidRequest.getBidAmount());
        bidRepository.save(bid);
        log.info("username: {} successfully placed bid: {}", customer.getUsername(), bid.getBidId());
    }

    @Override
    public List<Bid> getAuctionBids(String auctionId) {
        List<Bid> bids =  bidRepository.findByAuctionId(auctionId);
        return bids;
    }

    @Override
    public List<Bid> getBidsByAuctionIdAndCustomerId(String customerUsername, String auctionId) {
        List<Bid> bids = bidRepository.findByCustomer_UsernameAndAuction_AuctionId(customerUsername, auctionId);
        return bids;
    }

    @Override
    public List<Bid> getBidsByUsername(String customerUsername) {
        List<Bid> bids = bidRepository.findByCustomer_Username(customerUsername);
        return bids;
    }
}
