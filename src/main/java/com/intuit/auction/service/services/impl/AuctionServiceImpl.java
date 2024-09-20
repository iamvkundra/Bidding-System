package com.intuit.auction.service.services.impl;

import java.util.Optional;

import com.intuit.auction.service.entity.Auction;
import com.intuit.auction.service.entity.account.Account;
import com.intuit.auction.service.dto.AuctionRequest;
import com.intuit.auction.service.entity.account.Customer;
import com.intuit.auction.service.entity.account.Vendor;
import com.intuit.auction.service.repositories.AuctionRepository;
import com.intuit.auction.service.repositories.ProductRepository;
import com.intuit.auction.service.services.AccountService;
import com.intuit.auction.service.services.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuctionServiceImpl implements AuctionService {

    private AccountService accountService;
    private AuctionRepository auctionRepository;
    private ProductRepository productRepository;

    @Autowired
    public AuctionServiceImpl(AccountService auctionService, AuctionRepository auctionRepository,
                              ProductRepository productRepository) {
        this.accountService = auctionService;
        this.auctionRepository = auctionRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void createAuction(AuctionRequest auctionRequest) throws Exception {
        Account account = accountService.getUserByUsername(auctionRequest.getVendorUsername());

        if ((account instanceof Customer)) throw new Exception("");

        Auction auction = new Auction((Vendor) account, auctionRequest.getProduct(), auctionRequest.getStartTime(),
                auctionRequest.getEndTime(),auctionRequest.getAuctionBasePrice());

        productRepository.save(auction.getProduct());
        auctionRepository.save(auction);
    }
}
