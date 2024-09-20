package com.intuit.auction.service.model.Account;

import com.intuit.auction.service.model.User;
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
