package com.intuit.auction.service.exceptions;

public class AlreadyRegisteredException extends RuntimeException {
    public AlreadyRegisteredException(String message) {
        super(message);
    }
    public AlreadyRegisteredException(String message, Throwable throwable) {
        super(message);
    }
}
