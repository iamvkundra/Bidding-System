package com.intuit.auction.service.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        String message = "Account not found";
        AccountException exception = new AccountException(message);

        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void shouldCreateExceptionWithMessageAndCause() {
        String message = "Account update failed";
        Throwable cause = new IllegalArgumentException("Invalid account details");
        AccountException exception = new AccountException(message, cause);

        assertEquals(message, exception.getMessage());
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertTrue(exception instanceof RuntimeException);
    }
}