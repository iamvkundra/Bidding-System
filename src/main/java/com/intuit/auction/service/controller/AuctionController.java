package com.intuit.auction.service.controller;

import static com.intuit.auction.service.commons.Constants.AUCTION_SERVICE;

import java.util.List;

import com.intuit.auction.service.dto.AuctionFilter;
import com.intuit.auction.service.dto.AuctionRegistrationDto;
import com.intuit.auction.service.dto.AuctionRequest;
import com.intuit.auction.service.entity.Auction;
import com.intuit.auction.service.enums.AuctionStatus;
import com.intuit.auction.service.enums.ProductCategory;
import com.intuit.auction.service.services.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PostMapping("/register")
    public ResponseEntity<?> registerCustomerForAuction(@RequestBody AuctionRegistrationDto
                                                                    auctionRegistration) throws Exception {
        auctionService.registerUserForAuction(auctionRegistration);
        return ResponseEntity.ok("user successfully registered for auction.");
    }

    public ResponseEntity<?> updateAuction() {
        return null;
    }

//    @DeleteMapping
//    public ResponseEntity<?> closeAuction(@PathVariable String auctionId) {
//        return auctionService.closeAuction("", auctionId);
//    }

    @GetMapping
    public ResponseEntity<List<Auction>> searchAuction(@RequestParam(name = "categories", required = false)
                                                           List<ProductCategory> productCategories,
                                                       @RequestParam(name = "auctionStatus", required = false)
                                                       List<AuctionStatus> auctionStatuses) {
        AuctionFilter auctionFilter = new AuctionFilter(productCategories, auctionStatuses);
        List<Auction> auctions = auctionService.searchAuction(auctionFilter);
        return ResponseEntity.ok(auctions);
    }
}
