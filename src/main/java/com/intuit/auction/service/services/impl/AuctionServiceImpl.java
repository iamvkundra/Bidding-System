package com.intuit.auction.service.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.intuit.auction.service.dto.AccountResponseDto;
import com.intuit.auction.service.dto.AuctionFilter;
import com.intuit.auction.service.dto.AuctionRegistrationDto;
import com.intuit.auction.service.entity.Auction;
import com.intuit.auction.service.entity.AuctionRegistration;
import com.intuit.auction.service.entity.account.Account;
import com.intuit.auction.service.dto.AuctionRequest;
import com.intuit.auction.service.entity.account.Customer;
import com.intuit.auction.service.entity.account.Vendor;
import com.intuit.auction.service.enums.AccountType;
import com.intuit.auction.service.enums.AuctionStatus;
import com.intuit.auction.service.exceptions.AccountNotFoundException;
import com.intuit.auction.service.repositories.AuctionRegistrationRepository;
import com.intuit.auction.service.repositories.AuctionRepository;
import com.intuit.auction.service.repositories.ProductRepository;
import com.intuit.auction.service.services.AccountService;
import com.intuit.auction.service.services.AuctionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuctionServiceImpl implements AuctionService {

    private static final Logger log = LoggerFactory.getLogger(AuctionServiceImpl.class);
    private final AccountService accountService;
    private final AuctionRepository auctionRepository;
    private final AuctionRegistrationRepository auctionRegistrationRepository;
    private final ProductRepository productRepository;

    @Autowired
    public AuctionServiceImpl(AccountService auctionService, AuctionRepository auctionRepository,
                              ProductRepository productRepository, AuctionRegistrationRepository auctionRegistrationRepository) {
        this.accountService = auctionService;
        this.auctionRepository = auctionRepository;
        this.productRepository = productRepository;
        this.auctionRegistrationRepository = auctionRegistrationRepository;
    }

    @Override
    public void createAuction(AuctionRequest auctionRequest) throws Exception {
        Account account = accountService.getAccount(auctionRequest.getVendorUsername());

        if (account == null || (account instanceof Customer))
            throw new AccountNotFoundException("Account does not Exist or Customer account cannot create Auction.");

        Auction auction = new Auction((Vendor) account, auctionRequest.getProduct(), auctionRequest.getStartTime(),
                auctionRequest.getEndTime(),auctionRequest.getAuctionBasePrice());

        if (auction.getEndTime().isBefore(LocalDateTime.now())) {
            throw new Exception("Cannot Create Auction with before time");
        }

        if (auction.getStartTime().isBefore(LocalDateTime.now())) {
            auction.setAuctionStatus(AuctionStatus.ACTIVE);
        }else if (auction.getStartTime().isAfter(LocalDateTime.now())) {
            auction.setAuctionStatus(AuctionStatus.SCHEDULED);
        }

        productRepository.save(auction.getProduct());
        auctionRepository.save(auction);
    }

    @Override
    public void closeAuction(String vendorUsername, String auctionId) throws Exception {
        boolean checkIfVendorIsCorrectForGivenAuction = auctionRepository
                .checkAuctionByVendorUsername(vendorUsername, auctionId);
        if (checkIfVendorIsCorrectForGivenAuction) {
            auctionRepository.updateAuctionStatusByAuctionId(AuctionStatus.CLOSED, auctionId);
        }else {
            throw new Exception("Not a Vendor who created the auction.");
        }
    }

    @Override
    public List<Auction> searchAuction(AuctionFilter filter) {
        return auctionRepository.findByProductCategoryInAndAuctionStatusIn(
                filter.getProductCategory(),
                filter.getAuctionStatuses()
        );
    }

    @Override
    public Auction getAuctionDetails(String auctionId) {
        Optional<Auction> auction =  auctionRepository.findById(auctionId);
        return auction.isPresent() ? auction.get() : null;
    }

    @Override
    public void registerUserForAuction(AuctionRegistrationDto auctionRegistration)
            throws Exception {
        Account account = accountService.getAccount(auctionRegistration.getUsername());
        if (account instanceof Vendor) throw new Exception("Vendor can't register for auction");
        Optional<Auction> auction = auctionRepository.findById(auctionRegistration.getAuctionId());
        auctionRegistrationRepository.save(new AuctionRegistration((Customer) account, auction.get()));
        log.info("Username: {} Successfully Registered for auctionId: {}", account.getUsername(),
                auctionRegistration.getAuctionId());
    }

    @Override
    public boolean isUserRegisteredForAuction(String customerId, String auctionId) throws Exception {
        Account account = accountService.getAccount(customerId);
        if (account instanceof Vendor) throw new Exception("Vendor Cannot register into auction");

        Optional<Auction> auction = auctionRepository.findById(auctionId);
        if (!auction.isPresent()) {
            throw new Exception("Invalid Auction Id");
        }
        return auctionRegistrationRepository.existsByCustomerUsernameAndAuctionId((Customer) account, auction.get());
    }
}
