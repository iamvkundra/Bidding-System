package com.intuit.auction.service.exceptions;

public class BiddingException extends RuntimeException {
    public BiddingException(String message) {
        super(message);
    }
    public BiddingException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
