package com.intuit.auction.service.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionHandlerTest {

    private final ExceptionHandler exceptionHandler = new ExceptionHandler();

    @Test
    void shouldHandleAccountException() {
        String exceptionMessage = "Account not found";
        AccountException accountException = new AccountException(exceptionMessage);

        ResponseEntity<String> response = exceptionHandler.handleAccountException(accountException);

        assertEquals(exceptionMessage, response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldHandleGenericException() {
        String exceptionMessage = "Internal server error";
        Exception genericException = new Exception(exceptionMessage);

        ResponseEntity<String> response = exceptionHandler.handlerGenericException(genericException);

        assertEquals(exceptionMessage, response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}