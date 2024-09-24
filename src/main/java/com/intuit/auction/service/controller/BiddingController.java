package com.intuit.auction.service.controller;

import static com.intuit.auction.service.commons.Constants.BIDDING_SERVICE;

import java.security.Principal;
import java.util.List;

import com.intuit.auction.service.dto.BidRequest;
import com.intuit.auction.service.dto.BidResponse;
import com.intuit.auction.service.exceptions.AccessDeniedException;
import com.intuit.auction.service.exceptions.BadRequestException;
import com.intuit.auction.service.exceptions.BiddingException;
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
    @Operation(summary = "Place a Bid",
            description = "Place bid on Auction by Customer")
    @PreAuthorize("isAuthenticated() and hasAuthority('CUSTOMER')")
    public ResponseEntity<?> placeBid(Principal principal, @RequestBody BidRequest bidRequest) throws Exception {
        try {
            biddingService.placeBid(principal.getName(), bidRequest);
        } catch (BiddingException e) {
          return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Bid successfully placed", HttpStatus.CREATED);
    }

    @GetMapping("/{auctionId}")
    @Operation(summary = "Get All Bids By AuctionId",
            description = "Get all the bids for an auction, Only Vendor has access to this.")
    @PreAuthorize("isAuthenticated() and hasAuthority('VENDOR')")
    public ResponseEntity<?> getAllBidByAuctionId(@PathVariable("auctionId") String auctionId) {
        try {
            List<BidResponse> bids = biddingService.getAuctionBids(auctionId);
            return new ResponseEntity<>(bids, HttpStatus.OK);
        } catch (BiddingException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/auction/{auctionId}/account/{username}")
    @Operation(summary = "Get the bids by auctionId and ",
            description = "Get all the bids placed by customer for a specified auction Id")
    @PreAuthorize("hasAuthority('VENDOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<List<BidResponse>> getAllBidByAuctionIdByCustomerUsername(@PathVariable(name = "auctionId")
                                                                                 String auctionId,
                                                                             @PathVariable(name = "username")
                                                                             String customerUsername) {
        List<BidResponse> bids = biddingService.getBidsByAuctionIdAndCustomerId(customerUsername, auctionId);
        return new ResponseEntity<>(bids, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get All Bids(Customer)",
            description = "Get the all the placed for multiple auctions")
    @PreAuthorize("isAuthenticated() and hasAuthority('CUSTOMER')")
    public ResponseEntity< List<BidResponse>> getAllBids(Principal principal) {
        List<BidResponse> bids = biddingService.getBidsByUsername(principal.getName());
        return new ResponseEntity<>(bids, HttpStatus.OK);
    }

    @GetMapping("/auction/{auctionId}/winner")
    @Operation(summary = "Get the winning bid for an auction",
            description = "Get the winning bid for the specified auction ID")
    @PreAuthorize("isAuthenticated() and (hasAuthority('VENDOR') or hasAuthority('CUSTOMER'))")
    public ResponseEntity<?> getWinningBid(@PathVariable("auctionId") String auctionId) {
        try {
            BidResponse winningBid = biddingService.getWinningBid(auctionId);
            if (winningBid == null) {
                return new ResponseEntity<>("Winner is not declared Yet", HttpStatus.OK);
            }
            return new ResponseEntity<>(winningBid, HttpStatus.OK);
        } catch (BiddingException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
