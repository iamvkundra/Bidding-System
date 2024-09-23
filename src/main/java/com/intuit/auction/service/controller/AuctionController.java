package com.intuit.auction.service.controller;

import static com.intuit.auction.service.commons.Constants.AUCTION_SERVICE;

import java.security.Principal;
import java.util.List;


import com.intuit.auction.service.dto.AuctionFilter;
import com.intuit.auction.service.dto.AuctionRegistrationDto;
import com.intuit.auction.service.dto.AuctionRequest;
import com.intuit.auction.service.dto.AuctionResponseDto;
import com.intuit.auction.service.enums.AuctionStatus;
import com.intuit.auction.service.enums.ProductCategory;
import com.intuit.auction.service.services.AuctionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @Operation(summary = "Create Auction",
            description = "Auction will be only created by vendor")
    @PreAuthorize("isAuthenticated() and hasAuthority('VENDOR')")
    public ResponseEntity<?> createAuction(Principal principal,
                                           @RequestBody AuctionRequest auctionRequest) throws Exception {
        auctionService.createAuction(principal.getName(), auctionRequest);
        return ResponseEntity.ok("Auction Created");
    }

    @PostMapping("/register")
    @Operation(summary = "Register customer into auction",
            description = "Register customer into auction to start bidding")
    @PreAuthorize("isAuthenticated() and hasAuthority('CUSTOMER')")
    public ResponseEntity<?> registerCustomerForAuction(Principal principal,
                                                        @RequestBody @Valid AuctionRegistrationDto
                                                                    auctionRegistration) throws Exception {
        auctionService.registerUserForAuction(principal.getName(), auctionRegistration.getAuctionId());
        return ResponseEntity.ok("user successfully registered for auction.");
    }

    @DeleteMapping
    @Operation(summary = "Close auction",
            description = "Auction will be closed by same vendor.")
    @PreAuthorize("isAuthenticated() and hasAuthority('VENDOR')")
    public ResponseEntity<?> closeAuction(Principal principal, @PathVariable("auctionId") String auctionId) throws Exception {
        auctionService.closeAuction(principal.getName(), auctionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Search Auction",
            description = "Search auction based on filters")
    @PreAuthorize("isAuthenticated() and (hasAuthority('CUSTOMER') or hasAuthority('VENDOR'))")
    public ResponseEntity<List<AuctionResponseDto>> searchAuction(@RequestParam(name = "categories", required = false)
                                                           List<ProductCategory> productCategories,
                                                                  @RequestParam(name = "auctionStatus", required = false)
                                                       List<AuctionStatus> auctionStatuses) {
        AuctionFilter auctionFilter = new AuctionFilter(productCategories, auctionStatuses);
        List<AuctionResponseDto> auctions = auctionService.searchAuction(auctionFilter);
        return ResponseEntity.ok(auctions);
    }

    @GetMapping("/{auctionId}")
    @Operation(summary = "Get an auction details by auction id",
            description = "Get an auction details by auction id")
    @PreAuthorize("isAuthenticated() and (hasAuthority('CUSTOMER') or hasAuthority('VENDOR'))")
    public ResponseEntity<AuctionResponseDto> getAuctionById(@PathVariable(name = "auctionId") String auctionId) {
        return new ResponseEntity<>(auctionService.getAuctionById(auctionId), HttpStatus.OK);
    }
}
