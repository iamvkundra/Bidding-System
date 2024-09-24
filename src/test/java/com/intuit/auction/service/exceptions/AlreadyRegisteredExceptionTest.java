package com.intuit.auction.service.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlreadyRegisteredExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        String message = "User already registered";
        AlreadyRegisteredException exception = new AlreadyRegisteredException(message);

        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void shouldCreateExceptionWithMessageAndCause() {
        String message = "User already registered with email";
        Throwable cause = new IllegalArgumentException("Duplicate email");
        AlreadyRegisteredException exception = new AlreadyRegisteredException(message, cause);

        assertEquals(message, exception.getMessage());
    }
}