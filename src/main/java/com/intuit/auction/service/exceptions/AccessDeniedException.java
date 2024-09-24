package com.intuit.auction.service.exceptions;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
    public AccessDeniedException(String message, Throwable throwable) {
        super(message);
    }
}
