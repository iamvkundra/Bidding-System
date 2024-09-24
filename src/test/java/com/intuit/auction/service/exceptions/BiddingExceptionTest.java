package com.intuit.auction.service.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BiddingExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        String message = "Bid amount is too low";
        BiddingException exception = new BiddingException(message);

        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void shouldCreateExceptionWithMessageAndCause() {
        String message = "Bidding on closed auction is not allowed";
        Throwable cause = new IllegalStateException("Auction is closed");
        BiddingException exception = new BiddingException(message, cause);

        assertEquals(message, exception.getMessage());
        assertTrue(exception.getCause() instanceof IllegalStateException);
        assertTrue(exception instanceof RuntimeException);
    }
}