package com.intuit.auction.service.enums;

public enum AccountType {
    VENDOR,
    CUSTOMER;

    public static AccountType getAccountType(String value) {
        return value.equals(String.valueOf(VENDOR)) ? VENDOR : CUSTOMER;
    }
}
