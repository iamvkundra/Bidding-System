package com.intuit.auction.service.entity.account;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("VENDOR")
public class Vendor extends Account {

    public Vendor() { }

    public Vendor(String username, String password, User user) {
        super(username, password, user);
    }
}
