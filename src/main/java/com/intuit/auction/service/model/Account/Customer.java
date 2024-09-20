package com.intuit.auction.service.model.Account;

import com.intuit.auction.service.enums.AccountType;
import com.intuit.auction.service.model.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends Account {

    public Customer() { }

    public Customer(String username, String password, User user) {
        super(username, password, user);
    }
}
