package com.intuit.auction.service.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

public class AuctionRegistrationDtoTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testAuctionIdNotBlank() {
        AuctionRegistrationDto dto = new AuctionRegistrationDto();
        Set<ConstraintViolation<AuctionRegistrationDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Auction Id is Required", violations.iterator().next().getMessage());
    }

    @Test
    public void testAuctionIdValid() {
        AuctionRegistrationDto dto = new AuctionRegistrationDto();
        dto.setAuctionId("12345");
        Set<ConstraintViolation<AuctionRegistrationDto>> violations = validator.validate(dto);
        assertEquals(0, violations.size());
    }
}
