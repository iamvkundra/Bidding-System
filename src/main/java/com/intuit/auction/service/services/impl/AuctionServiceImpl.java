package com.intuit.auction.service.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.intuit.auction.service.dto.AuctionFilter;
import com.intuit.auction.service.dto.AuctionResponseDto;
import com.intuit.auction.service.entity.Auction;
import com.intuit.auction.service.entity.AuctionRegistration;
import com.intuit.auction.service.entity.account.Account;
import com.intuit.auction.service.dto.AuctionRequest;
import com.intuit.auction.service.entity.account.Customer;
import com.intuit.auction.service.entity.account.Vendor;
import com.intuit.auction.service.enums.AuctionStatus;
import com.intuit.auction.service.jobs.AuctionManagement;
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
    private final AuctionManagement auctionManagement;

    @Autowired
    public AuctionServiceImpl(AccountService auctionService, AuctionRepository auctionRepository,
                              ProductRepository productRepository, AuctionRegistrationRepository
                                          auctionRegistrationRepository, AuctionManagement auctionManagement) {
        this.accountService = auctionService;
        this.auctionRepository = auctionRepository;
        this.productRepository = productRepository;
        this.auctionRegistrationRepository = auctionRegistrationRepository;
        this.auctionManagement = auctionManagement;
    }

    @Override
    public void createAuction(String username, AuctionRequest auctionRequest) throws Exception {
        try {
            Account account = accountService.getAccount(username);

            Auction auction = new Auction((Vendor) account, auctionRequest.getProduct(), auctionRequest.getStartTime(),
                    auctionRequest.getEndTime(), auctionRequest.getAuctionBasePrice());

            if (auction.getEndTime().isBefore(LocalDateTime.now())) {
                throw new Exception("Cannot Create Auction with before time");
            }

            if (auction.getStartTime().isBefore(LocalDateTime.now())) {
                auction.setAuctionStatus(AuctionStatus.ACTIVE);
            } else if (auction.getStartTime().isAfter(LocalDateTime.now())) {
                auction.setAuctionStatus(AuctionStatus.SCHEDULED);
            }

            productRepository.save(auction.getProduct());
            auctionRepository.save(auction);
            auctionManagement.addAuction(auction.getAuctionId(), auction.getStartTime(), auction.getEndTime(),
                    auction.getAuctionStatus());

        } catch (Exception exception) {
            throw new Exception("Something went wrong: ", exception);
        }
    }

    @Override
    public void closeAuction(String username, String auctionId) throws Exception {
        boolean checkIfVendorIsCorrectForGivenAuction = auctionRepository
                .checkAuctionByVendorUsername(username, auctionId);

        if (checkIfVendorIsCorrectForGivenAuction) {
            auctionRepository.updateAuctionStatusByAuctionId(AuctionStatus.CLOSED, auctionId);
        }else {
            throw new Exception("Not a Vendor who created the auction.");
        }
    }

    @Override
    public AuctionResponseDto getAuctionById(String auctionId) {
        Optional<Auction> auction = auctionRepository.findById(auctionId);
        return auction.map(this::createAuctionResponseId).orElse(null);
    }

    @Override
    public List<AuctionResponseDto> searchAuction(AuctionFilter filter) {
        return auctionRepository.findByProductCategoryInAndAuctionStatusIn(
                filter.getProductCategory(),
                filter.getAuctionStatuses()
        ).stream().map(this::createAuctionResponseId).toList();
    }

    @Override
    public Auction getAuctionDetails(String auctionId) {
        Optional<Auction> auction =  auctionRepository.findById(auctionId);
        return auction.orElse(null);
    }

    @Override
    public void registerUserForAuction(String username, String auctionId)
            throws Exception {
        try {
            Account account = accountService.getAccount(username);
            Optional<Auction> auction = auctionRepository.findById(auctionId);

            boolean checkAlreadyRegistered = auctionRegistrationRepository
                    .existsByCustomerUsernameAndAuctionId((Customer) account, auction.get());

            if (checkAlreadyRegistered) {
                throw new Exception("This account is already registered for auction: " + auctionId);
            }
            auctionRegistrationRepository.save(new AuctionRegistration((Customer) account, auction.get()));
            log.info("Username: {} Successfully Registered for auctionId: {}", account.getUsername(),
                    auctionId);
        } catch (Exception e) {
            log.error("Something went wrong while registering user for auction", e.getCause());
            throw new Exception(e);
        }
    }

    @Override
    public boolean isUserRegisteredForAuction(String customerId, String auctionId) throws Exception {
        Account account = accountService.getAccount(customerId);
        Optional<Auction> auction = auctionRepository.findById(auctionId);

        if (auction.isEmpty()) {
            log.error("Auction id is invalid. Not Found");
            throw new Exception("Invalid Auction Id");
        }
        return auctionRegistrationRepository.existsByCustomerUsernameAndAuctionId((Customer) account, auction.get());
    }

    private AuctionResponseDto createAuctionResponseId(Auction auction) {
        AuctionResponseDto auctionResponseDto = new AuctionResponseDto();
        auctionResponseDto.setAuctionId(auction.getAuctionId());
        auctionResponseDto.setProduct(auction.getProduct());
        auctionResponseDto.setEndTime(auction.getEndTime());
        auctionResponseDto.setAuctionBasePrice(auction.getAuctionBasePrice());
        auctionResponseDto.setHighestBid(auction.getHighestBid() == null ? 0.0 : auction.getHighestBid().getAmount());
        auctionResponseDto.setStartTime(auction.getStartTime());
        auctionResponseDto.setAuctionStatus(auction.getAuctionStatus());
        auctionResponseDto.setVendorUsername(auction.getVendor() != null ? auction.getVendor().getUsername() : null);
        return auctionResponseDto;
    }

}
