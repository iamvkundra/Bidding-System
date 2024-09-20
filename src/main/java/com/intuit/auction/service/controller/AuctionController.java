package com.intuit.auction.service.controller;

import com.intuit.auction.service.services.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class AuctionController {

    @Autowired
    private AuctionService auctionService;

    public ResponseEntity<?> createAuction() {
        return null;
    }

    public ResponseEntity<?> updateAuction() {
        return null;
    }

    public ResponseEntity<?> removeAuction() {
        return null;
    }
}
