package com.intuit.auction.service.controller;

import static com.intuit.auction.service.commons.Constants.AUCTION_SERVICE;

import com.intuit.auction.service.dto.AuctionRequest;
import com.intuit.auction.service.services.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AUCTION_SERVICE)
public class AuctionController {

    @Autowired
    private AuctionService auctionService;

    @PostMapping
    public ResponseEntity<?> createAuction(@RequestBody AuctionRequest auctionRequest) throws Exception {
        auctionService.createAuction(auctionRequest);
        return ResponseEntity.ok("Auction Created");
    }

    public ResponseEntity<?> updateAuction() {
        return null;
    }

    public ResponseEntity<?> removeAuction() {
        return null;
    }
}
