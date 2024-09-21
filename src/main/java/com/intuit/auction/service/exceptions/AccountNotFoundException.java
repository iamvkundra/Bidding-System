package com.intuit.auction.service.exceptions;

public class AccountNotFoundException extends Exception {
    public AccountNotFoundException (String message) {
        super("Account Not Found: " + message);
    }
}
