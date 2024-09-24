package com.intuit.auction.service.controller;

import com.intuit.auction.service.dto.AuctionRegistrationDto;
import com.intuit.auction.service.dto.AuctionRequest;
import com.intuit.auction.service.dto.AuctionResponseDto;
import com.intuit.auction.service.entity.Product;
import com.intuit.auction.service.enums.AuctionStatus;
import com.intuit.auction.service.enums.ProductCategory;
import com.intuit.auction.service.exceptions.AccessDeniedException;
import com.intuit.auction.service.exceptions.AlreadyRegisteredException;
import com.intuit.auction.service.exceptions.BadRequestException;
import com.intuit.auction.service.services.AuctionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuctionControllerTest {

    @InjectMocks
    private AuctionController auctionController;

    @Mock
    private AuctionService auctionService;

    @Mock
    private Principal principal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAuction_success() {
        when(principal.getName()).thenReturn("vendor1");
        AuctionRequest request = new AuctionRequest(); // Set up your request object here

        ResponseEntity<?> response = auctionController.createAuction(principal, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Auction Created", response.getBody());

    }

    @Test
    void registerCustomerForAuction_success() throws Exception {
        when(principal.getName()).thenReturn("customer1");
        AuctionRegistrationDto registrationDto = new AuctionRegistrationDto();
        registrationDto.setAuctionId("auctionId1");

        ResponseEntity<?> response = auctionController.registerCustomerForAuction(principal, registrationDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("user successfully registered for auction.", response.getBody());
        verify(auctionService, times(1)).registerUserForAuction("customer1", "auctionId1");
    }

    @Test
    void closeAuction_success() throws Exception {
        when(principal.getName()).thenReturn("vendor1");
        String auctionId = "auctionId1";

        ResponseEntity<?> response = auctionController.closeAuction(principal, auctionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(auctionService, times(1)).closeAuction("vendor1", auctionId);
    }

    @Test
    void searchAuction_success() {
        List<ProductCategory> categories = Collections.singletonList(ProductCategory.ELECTRONIC);
        List<AuctionStatus> statuses = Collections.singletonList(AuctionStatus.ACTIVE);
        when(principal.getName()).thenReturn("user1");
        when(auctionService.searchAuction(any())).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = auctionController.searchAuction(categories, statuses);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
    }

    @Test
    void getAuctionById_success() {
        String auctionId = "auctionId1";
        AuctionResponseDto responseDto = new AuctionResponseDto(); // Populate this with expected data
        when(auctionService.getAuctionById(auctionId)).thenReturn(responseDto);

        ResponseEntity<AuctionResponseDto> response = auctionController.getAuctionById(auctionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
    }
}
