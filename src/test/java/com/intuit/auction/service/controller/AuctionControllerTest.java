package com.intuit.auction.service.controller;

import com.intuit.auction.service.dto.AuctionRegistrationDto;
import com.intuit.auction.service.dto.AuctionRequest;
import com.intuit.auction.service.dto.AuctionResponseDto;
import com.intuit.auction.service.services.AuctionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuctionControllerTest {

    @Mock
    private AuctionService auctionService;

    @InjectMocks
    private AuctionController auctionController;

    private Principal principal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        principal = mock(Principal.class);
        when(principal.getName()).thenReturn("vendor1");
    }

    @Test
    void createAuction_shouldReturnOkStatus() throws Exception {
        AuctionRequest auctionRequest = new AuctionRequest();
        // Set properties for auctionRequest as needed

        ResponseEntity<?> response = auctionController.createAuction(principal, auctionRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Auction Created");
        verify(auctionService).createAuction("vendor1", auctionRequest);
    }

    @Test
    void registerCustomerForAuction_shouldReturnOkStatus() throws Exception {
        AuctionRegistrationDto auctionRegistration = new AuctionRegistrationDto();
        auctionRegistration.setAuctionId("auctionId");

        ResponseEntity<?> response = auctionController.registerCustomerForAuction(principal, auctionRegistration);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("user successfully registered for auction.");
        verify(auctionService).registerUserForAuction("vendor1", "auctionId");
    }

    @Test
    void closeAuction_shouldReturnOkStatus() throws Exception {
        String auctionId = "auctionId";

        ResponseEntity<?> response = auctionController.closeAuction(principal, auctionId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(auctionService).closeAuction("vendor1", auctionId);
    }

    @Test
    void searchAuction_shouldReturnOkStatus() {
        List<AuctionResponseDto> auctions = Arrays.asList(new AuctionResponseDto(), new AuctionResponseDto());
        when(auctionService.searchAuction(any())).thenReturn(auctions);

        ResponseEntity<List<AuctionResponseDto>> response = auctionController.searchAuction(null, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(auctions);
    }

    @Test
    void getAuctionById_shouldReturnOkStatus() {
        String auctionId = "auctionId";
        AuctionResponseDto auctionResponseDto = new AuctionResponseDto();
        when(auctionService.getAuctionById(auctionId)).thenReturn(auctionResponseDto);

        ResponseEntity<AuctionResponseDto> response = auctionController.getAuctionById(auctionId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(auctionResponseDto);
    }
}
