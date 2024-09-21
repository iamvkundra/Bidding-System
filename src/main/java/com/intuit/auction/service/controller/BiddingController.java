package com.intuit.auction.service.controller;

import static com.intuit.auction.service.commons.Constants.BIDDING_SERVICE;

import java.util.List;

import com.intuit.auction.service.dto.BidRequest;
import com.intuit.auction.service.entity.Bid;
import com.intuit.auction.service.services.BiddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BIDDING_SERVICE)
public class BiddingController {

    @Autowired
    private BiddingService biddingService;

    @PostMapping
    public ResponseEntity<?> placeBid(@RequestBody BidRequest bidRequest) throws Exception {
        biddingService.placeBid(bidRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{auctionId}")
    public ResponseEntity<List<Bid>> getAllBidByAuctionId(String auctionId) {
        List<Bid> bids = biddingService.getAuctionBids(auctionId);
        return new ResponseEntity<>(bids, HttpStatus.OK);
    }

    @GetMapping("/auction/{auctionId}/account/{customerUsername}")
    public ResponseEntity<List<Bid>> getAllBidByAuctionIdAndCustomerUsername(@PathVariable(name = "auctionId")
                                                                                 String auctionId,
                                                                             @PathVariable(name = "customerUsername")
                                                                             String customerUsername) {
        List<Bid> bids = biddingService.getBidsByAuctionIdAndCustomerId(customerUsername, auctionId);
        return new ResponseEntity<>(bids, HttpStatus.OK);
    }

    @GetMapping("/account/{customerUsername}")
    public ResponseEntity<?> getBidsByUsername(@PathVariable(name = "customerUsername") String username) {
        List<Bid> bids = biddingService.getBidsByUsername(username);
        return new ResponseEntity<>(bids, HttpStatus.OK);
    }

}
