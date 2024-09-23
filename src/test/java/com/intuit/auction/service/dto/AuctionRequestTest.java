package com.intuit.auction.service.dto;

import com.intuit.auction.service.entity.Product;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuctionRequestTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testAuctionRequestWithNullFields() {
        AuctionRequest auctionRequest = new AuctionRequest();
        Set<ConstraintViolation<AuctionRequest>> violations = validator.validate(auctionRequest);

        assertEquals(violations.size(), 0);
    }

    @Test
    public void testAuctionRequestValid() {
        AuctionRequest auctionRequest = new AuctionRequest();
        auctionRequest.setProduct(new Product()); // Assuming Product has a default constructor
        auctionRequest.setStartTime(LocalDateTime.now());
        auctionRequest.setEndTime(LocalDateTime.now().plusHours(1));
        auctionRequest.setAuctionBasePrice(100.0);

        Set<ConstraintViolation<AuctionRequest>> violations = validator.validate(auctionRequest);
        assertEquals(0, violations.size());
    }
}
