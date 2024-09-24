package com.intuit.auction.service.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountNotFoundExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        String message = "12345";
        AccountNotFoundException exception = new AccountNotFoundException(message);

        assertEquals("Account Not Found: " + message, exception.getMessage());
        assertTrue(exception instanceof Exception);
    }
}