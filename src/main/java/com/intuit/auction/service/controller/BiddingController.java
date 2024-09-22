package com.intuit.auction.service.controller;

import static com.intuit.auction.service.commons.Constants.BIDDING_SERVICE;

import java.security.Principal;
import java.util.List;

import com.intuit.auction.service.dto.BidRequest;
import com.intuit.auction.service.dto.BidResponse;
import com.intuit.auction.service.services.BiddingService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @Operation(summary = "Place Bid",
            description = "Place bid on Auction by Customer")
    @PreAuthorize("isAuthenticated() and hasAuthority('CUSTOMER')")
    public ResponseEntity<?> placeBid(Principal principal, @RequestBody BidRequest bidRequest) throws Exception {
        try {
            biddingService.placeBid(principal.getName(), bidRequest);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{auctionId}")
    @PreAuthorize("hasAuthority('VENDOR')")
    public ResponseEntity<List<BidResponse>> getAllBidByAuctionId(@PathVariable("auctionId") String auctionId) {
        List<BidResponse> bids = biddingService.getAuctionBids(auctionId);
        return new ResponseEntity<>(bids, HttpStatus.OK);
    }

    @GetMapping("/auction/{auctionId}/account/{customerUsername}")
    @PreAuthorize("hasAuthority('VENDOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<List<BidResponse>> getAllBidByAuctionIdAndCustomerUsername(@PathVariable(name = "auctionId")
                                                                                 String auctionId,
                                                                             @PathVariable(name = "customerUsername")
                                                                             String customerUsername) {
        List<BidResponse> bids = biddingService.getBidsByAuctionIdAndCustomerId(customerUsername, auctionId);
        return new ResponseEntity<>(bids, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated() and hasAuthority('CUSTOMER')")
    public ResponseEntity< List<BidResponse>> getBidsFor(Principal principal) {
        List<BidResponse> bids = biddingService.getBidsByUsername(principal.getName());
        return new ResponseEntity<>(bids, HttpStatus.OK);
    }
}
