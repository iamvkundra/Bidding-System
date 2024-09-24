package com.intuit.auction.service.services;


import com.intuit.auction.service.dto.AuctionRequest;
import com.intuit.auction.service.dto.AuctionResponseDto;
import com.intuit.auction.service.entity.Auction;
import com.intuit.auction.service.entity.AuctionRegistration;

import com.intuit.auction.service.entity.account.Account;
import com.intuit.auction.service.entity.account.Customer;
import com.intuit.auction.service.entity.account.Vendor;
import com.intuit.auction.service.enums.AuctionStatus;

import com.intuit.auction.service.exceptions.AccessDeniedException;
import com.intuit.auction.service.exceptions.AlreadyRegisteredException;
import com.intuit.auction.service.exceptions.BadRequestException;
import com.intuit.auction.service.jobs.AuctionManagement;
import com.intuit.auction.service.repositories.AuctionRegistrationRepository;
import com.intuit.auction.service.repositories.AuctionRepository;
import com.intuit.auction.service.repositories.ProductRepository;

import com.intuit.auction.service.services.impl.AuctionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuctionServiceImplTest {

    @Mock
    private AccountService accountService;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private AuctionRegistrationRepository auctionRegistrationRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AuctionManagement auctionManagement;

    private AuctionServiceImpl auctionService;

    @BeforeEach
    void setup() {
        auctionService = new AuctionServiceImpl(
                accountService, auctionRepository,
                productRepository, auctionRegistrationRepository,
                auctionManagement);
    }

    @Test
    void shouldCreateAuction() throws Exception {
        String username = "vendor";
        AuctionRequest auctionRequest = new AuctionRequest();
        auctionRequest.setStartTime(LocalDateTime.now().plusDays(1));
        auctionRequest.setEndTime(LocalDateTime.now().plusDays(2));
        auctionRequest.setAuctionBasePrice(100.0);

        Vendor vendor = new Vendor();
        vendor.setUsername(username);

        when(accountService.getAccount(username)).thenReturn(vendor);
        when(productRepository.save(any())).thenReturn(auctionRequest.getProduct());
        when(auctionRepository.save(any())).thenReturn(new Auction());

        auctionService.createAuction(username, auctionRequest);

        verify(auctionRepository, times(1)).save(any());
        verify(auctionManagement, times(1)).addAuction(anyString(), any(), any());
    }

    @Test
    void shouldThrowExceptionWhenAuctionTimesAreInvalid() {
        String username = "vendor";
        AuctionRequest auctionRequest = new AuctionRequest();
        auctionRequest.setStartTime(LocalDateTime.now().plusDays(2));
        auctionRequest.setEndTime(LocalDateTime.now().plusDays(1));
        auctionRequest.setAuctionBasePrice(100.0);
        Account vendor = new Vendor();
        vendor.setUsername(username);
        when(accountService.getAccount(username)).thenReturn(vendor);
        assertThrows(BadRequestException.class, () -> auctionService.createAuction(username, auctionRequest));
    }

    @Test
    void shouldCloseAuction() throws Exception {
        String username = "vendor";
        String auctionId = "123";

        when(auctionRepository.checkAuctionByVendorUsername(username, auctionId)).thenReturn(true);
        doNothing().when(auctionRepository).updateAuctionStatusByAuctionId(AuctionStatus.CLOSED, auctionId);

        auctionService.closeAuction(username, auctionId);

        verify(auctionRepository, times(1)).updateAuctionStatusByAuctionId(AuctionStatus.CLOSED, auctionId);
    }

    @Test
    void shouldThrowAccessDeniedExceptionWhenClosingAuctionNotCreatedByVendor() {
        String username = "vendor";
        String auctionId = "123";
        when(auctionRepository.checkAuctionByVendorUsername(username, auctionId)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> auctionService.closeAuction(username, auctionId));
    }

    @Test
    void shouldGetAuctionById() {
        Auction auction = new Auction();
        when(auctionRepository.findById(auction.getAuctionId())).thenReturn(Optional.of(auction));
        AuctionResponseDto response = auctionService.getAuctionById(auction.getAuctionId());
        assertNotNull(response);
        assertEquals(auction.getAuctionId(), response.getAuctionId());
    }



    @Test
    void registerCustomerForAuction() throws Exception {
        String username = "mayank";
        String auctionId = "abcbjhasjasjdhasd";

        Account customer = new Customer();
        customer.setUsername(username);

        Auction auction = new Auction();
        auction.setAuctionId(auctionId);

        when(accountService.getAccount(username)).thenReturn(customer);
        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        when(auctionRegistrationRepository.existsByCustomerUsernameAndAuctionId((Customer) customer,
                auction)).thenReturn(false);
        when(auctionRegistrationRepository.save(any())).thenReturn(any());
        auctionService.registerUserForAuction(username, auctionId);
        verify(auctionRegistrationRepository, times(1)).save(any(AuctionRegistration.class));
    }

    @Test
    void shouldThrowAlreadyRegisteredExceptionWhenUserIsAlreadyRegisteredForAuction() {
        String username = "mayank";
        String auctionId = "12asdas3";
        Account customer = new Customer();
        customer.setUsername(username);

        Auction auction = new Auction();
        auction.setAuctionId(auctionId);

        when(accountService.getAccount(username)).thenReturn(customer);
        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        when(auctionRegistrationRepository.existsByCustomerUsernameAndAuctionId((Customer) customer,
                auction)).thenReturn(true);

        assertThrows(AlreadyRegisteredException.class, () -> auctionService.registerUserForAuction(username, auctionId));
    }

    @Test
    void shouldCheckIfUserIsRegisteredForAuction() throws Exception {
        String customerId = "customer";
        String auctionId = "123";

        Customer customer = new Customer();
        customer.setUsername(customerId);

        Auction auction = new Auction();
        auction.setAuctionId(auctionId);

        when(accountService.getAccount(customerId)).thenReturn(customer);
        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        when(auctionRegistrationRepository.existsByCustomerUsernameAndAuctionId(customer, auction)).thenReturn(true);

        assertTrue(auctionService.isUserRegisteredForAuction(customerId, auctionId));
    }

    @Test
    void shouldThrowExceptionWhenAuctionIdIsInvalid() {
        String customerId = "customer";
        String auctionId = "123";

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> auctionService.isUserRegisteredForAuction(customerId, auctionId));
    }
}