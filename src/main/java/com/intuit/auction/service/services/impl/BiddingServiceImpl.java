package com.intuit.auction.service.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.intuit.auction.service.dto.AuctionResponseDto;
import com.intuit.auction.service.dto.BidRequest;
import com.intuit.auction.service.dto.BidResponse;
import com.intuit.auction.service.entity.Auction;
import com.intuit.auction.service.entity.Bid;
import com.intuit.auction.service.entity.account.Account;
import com.intuit.auction.service.entity.account.Customer;
import com.intuit.auction.service.entity.account.Vendor;
import com.intuit.auction.service.enums.AuctionStatus;
import com.intuit.auction.service.exceptions.AccessDeniedException;
import com.intuit.auction.service.exceptions.BadRequestException;
import com.intuit.auction.service.exceptions.BiddingException;
import com.intuit.auction.service.repositories.AuctionRepository;
import com.intuit.auction.service.repositories.BidRepository;
import com.intuit.auction.service.services.AccountService;
import com.intuit.auction.service.services.AuctionService;
import com.intuit.auction.service.services.BiddingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BiddingServiceImpl implements BiddingService {

    private static final Logger log = LoggerFactory.getLogger(BiddingServiceImpl.class);
    private final BidRepository bidRepository;
    private final AccountService accountService;
    private final AuctionService auctionService;
    private final AuctionRepository auctionRepository;

    @Autowired
    public BiddingServiceImpl(BidRepository bidRepository, AccountService accountService,
                              AuctionService auctionService, AuctionRepository auctionRepository) {
        this.bidRepository = bidRepository;
        this.accountService = accountService;
        this.auctionService = auctionService;
        this.auctionRepository = auctionRepository;
    }

    @Override
    public void placeBid(String username, BidRequest bidRequest) throws Exception {
        Account customer = accountService.getAccount(username);
        Auction auction = auctionService.getAuctionDetails(bidRequest.getAuctionId());
        validateBidding(customer,auction, bidRequest);

        Bid bid = new Bid(auction, (Customer) customer, bidRequest.getBidAmount());
        bidRepository.save(bid);

        updateAuctionWithNewBid(auction, bid);
        log.info("username: {} successfully placed bid: {}", customer.getUsername(), bid.getBidId());
    }

    @Override
    public List<BidResponse> getAuctionBids(String auctionId) throws BiddingException {
        try {
            List<Bid> bids = bidRepository.findByAuctionId(auctionId);
            return createBidResponse(bids);
        } catch (Exception exception) {
            throw new BiddingException("Something went wrong, Please also check auctionId");
        }

    }

    @Override
    public List<BidResponse> getBidsByAuctionIdAndCustomerId(String customerUsername, String auctionId) {
        List<Bid> bids = bidRepository.findByCustomer_UsernameAndAuction_AuctionId(customerUsername, auctionId);
        return createBidResponse(bids);
    }

    @Override
    public List<BidResponse> getBidsByUsername(String customerUsername) {
        List<Bid> bids = bidRepository.findByCustomer_Username(customerUsername);
        return createBidResponse(bids);
    }

    @Override
    public BidResponse getWinningBid(String auctionId) throws BiddingException {
        try {
            Optional<Auction> auction = auctionRepository.findById(auctionId);
            if (auction.isPresent() && auction.get().getWinningBid() != null) {
                return createBid(auction.get().getWinningBid());
            }
        } catch (BiddingException exception) {
            throw new BiddingException(exception.getMessage(), exception);
        }
        return null;
    }

    private void validateBidding(Account customer, Auction auction, BidRequest bidRequest) throws Exception {
        if (!auction.getAuctionStatus().equals(AuctionStatus.ACTIVE)) {
            throw new BiddingException("Cannot Bid into Scheduled or Closed Bid");
        }

        if (!auctionService.isUserRegisteredForAuction(customer.getUsername(), auction.getAuctionId())) {
            throw new AccessDeniedException("Cannot Bid, The customer need to register first");
        }

        if (auction.getAuctionBasePrice() > bidRequest.getBidAmount()) {
            throw new BadRequestException("Customer Bidding amount is less than auctionBaseAmount: " + auction.getAuctionBasePrice());
        }
    }

    private List<BidResponse> createBidResponse(List<Bid> bids) {
        List<BidResponse> response = new ArrayList<>();
        for (Bid bid : bids) {
            BidResponse bidResponse = new BidResponse();
            bidResponse.setBidAmount(bid.getAmount());
            bidResponse.setTimeOfBid(bid.getBidTime());
            bidResponse.setUsername(bid.getCustomer().getUsername());
            bidResponse.setBidId(bid.getBidId());
            response.add(bidResponse);
        }
        return response;
    }

    private BidResponse createBid(Bid bid) {
        BidResponse bidResponse = new BidResponse();
        bidResponse.setBidAmount(bid.getAmount());
        bidResponse.setTimeOfBid(bid.getBidTime());
        bidResponse.setUsername(bid.getCustomer().getUsername());
        bidResponse.setBidId(bid.getBidId());
        return bidResponse;

    }
    private void updateAuctionWithNewBid(Auction auction, Bid newBid) {
        if (auction.getHighestBid() == null || auction.getHighestBid().getAmount() < newBid.getAmount()) {
            auction.setHighestBid(newBid);
            auctionRepository.save(auction);
        }
    }
}
