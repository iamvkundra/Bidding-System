package com.intuit.auction.service.controller;

import com.intuit.auction.service.dto.BidRequest;
import com.intuit.auction.service.dto.BidResponse;
import com.intuit.auction.service.services.BiddingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

class BiddingControllerTest {

    @Mock
    private BiddingService biddingService;

    @InjectMocks
    private BiddingController biddingController;

    private Principal principal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        principal = mock(Principal.class);
        when(principal.getName()).thenReturn("customer1");
    }

    @Test
    void placeBid_shouldReturnCreatedStatus() throws Exception {
        BidRequest bidRequest = new BidRequest();
        // Set properties for bidRequest as needed

        ResponseEntity<?> response = biddingController.placeBid(principal, bidRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(biddingService).placeBid("customer1", bidRequest);
    }

    @Test
    void placeBid_shouldReturnBadRequestStatusOnException() throws Exception {
        BidRequest bidRequest = new BidRequest();
        // Set properties for bidRequest as needed
        doThrow(new RuntimeException("Error placing bid")).when(biddingService).placeBid(anyString(), any());

        ResponseEntity<?> response = biddingController.placeBid(principal, bidRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Error placing bid");
    }

    @Test
    void getAllBidByAuctionId_shouldReturnOkStatus() {
        String auctionId = "auction1";
        List<BidResponse> bids = Arrays.asList(new BidResponse(), new BidResponse());
        when(biddingService.getAuctionBids(auctionId)).thenReturn(bids);

        ResponseEntity<List<BidResponse>> response = biddingController.getAllBidByAuctionId(auctionId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(bids);
    }

    @Test
    void getAllBidByAuctionIdAndCustomerUsername_shouldReturnOkStatus() {
        String auctionId = "auction1";
        String customerUsername = "customer1";
        List<BidResponse> bids = Arrays.asList(new BidResponse(), new BidResponse());
        when(biddingService.getBidsByAuctionIdAndCustomerId(customerUsername, auctionId)).thenReturn(bids);

        ResponseEntity<List<BidResponse>> response = biddingController.getAllBidByAuctionIdAndCustomerUsername(auctionId, customerUsername);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(bids);
    }

    @Test
    void getBidsFor_shouldReturnOkStatus() {
        List<BidResponse> bids = Arrays.asList(new BidResponse(), new BidResponse());
        when(biddingService.getBidsByUsername("customer1")).thenReturn(bids);

        ResponseEntity<List<BidResponse>> response = biddingController.getBidsFor(principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(bids);
    }
}
