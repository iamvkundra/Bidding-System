package com.intuit.auction.service.services;

import com.intuit.auction.service.dto.BidRequest;
import com.intuit.auction.service.dto.BidResponse;
import com.intuit.auction.service.entity.Auction;
import com.intuit.auction.service.entity.Bid;
import com.intuit.auction.service.entity.account.Customer;

import com.intuit.auction.service.enums.AuctionStatus;
import com.intuit.auction.service.exceptions.AccessDeniedException;
import com.intuit.auction.service.exceptions.BiddingException;
import com.intuit.auction.service.repositories.AuctionRepository;
import com.intuit.auction.service.repositories.BidRepository;
import com.intuit.auction.service.services.impl.BiddingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BiddingServiceImplTest {

    @InjectMocks
    private BiddingServiceImpl biddingService;

    @Mock
    private BidRepository bidRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private AuctionService auctionService;

    @Mock
    private AuctionRepository auctionRepository;

    private Customer customer;
    private Auction auction;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        customer = new Customer();
        customer.setUsername("testUser");

        auction = new Auction();
        auction.setAuctionId("auction1");
        auction.setAuctionBasePrice(100.0);
        auction.setAuctionStatus(AuctionStatus.ACTIVE);
        auction.setHighestBid(null);
    }

    @Test
    public void testPlaceBidWhenAuctionIsNotActive() {
        auction.setAuctionStatus(AuctionStatus.SCHEDULED);
        BidRequest bidRequest = new BidRequest();
        bidRequest.setAuctionId("auction1");
        bidRequest.setBidAmount(150.0);

        when(accountService.getAccount("testUser")).thenReturn(customer);
        when(auctionService.getAuctionDetails("auction1")).thenReturn(auction);

        Exception exception = assertThrows(Exception.class, () -> {
            biddingService.placeBid("testUser", bidRequest);
        });

        assertEquals("Cannot Bid into Scheduled or Closed Bid", exception.getMessage());
    }

    @Test
    public void testPlaceBidWhenCustomerNotRegistered() throws Exception {
        BidRequest bidRequest = new BidRequest();
        bidRequest.setAuctionId("auction1");
        bidRequest.setBidAmount(150.0);

        when(accountService.getAccount("testUser")).thenReturn(customer);
        when(auctionService.getAuctionDetails("auction1")).thenReturn(auction);
        when(auctionService.isUserRegisteredForAuction("testUser", "auction1")).thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            biddingService.placeBid("testUser", bidRequest);
        });

        assertEquals("Cannot Bid, The customer need to register first", exception.getMessage());
    }


    @Test
    public void testGetBidsByAuctionIdAndCustomerId() {
        Bid bid = new Bid();
        bid.setAuction(auction);
        bid.setAmount(150.0);
        bid.setCustomer(customer);
        bid.setBidTime(LocalDateTime.now());

        when(bidRepository.findByCustomer_UsernameAndAuction_AuctionId("testUser", "auction1"))
                .thenReturn(Collections.singletonList(bid));

        List<BidResponse> responses = biddingService.getBidsByAuctionIdAndCustomerId("testUser", "auction1");

        assertEquals(1, responses.size());
        assertEquals(150.0, responses.get(0).getBidAmount());
    }

    @Test
    public void testGetBidsByUsername() {
        Bid bid = new Bid();
        bid.setAuction(auction);
        bid.setAmount(150.0);
        bid.setCustomer(customer);
        bid.setBidTime(LocalDateTime.now());

        when(bidRepository.findByCustomer_Username("testUser"))
                .thenReturn(Collections.singletonList(bid));

        List<BidResponse> responses = biddingService.getBidsByUsername("testUser");

        assertEquals(1, responses.size());
        assertEquals(150.0, responses.get(0).getBidAmount());
    }
}
