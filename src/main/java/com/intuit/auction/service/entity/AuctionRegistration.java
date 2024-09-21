package com.intuit.auction.service.entity;

import java.util.UUID;

import com.intuit.auction.service.entity.account.Customer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "auction_registrations")
public class AuctionRegistration {
    @Id
    private String id = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Customer customerUsername;

    @ManyToOne
    @JoinColumn(name = "auction_id")
    private Auction auctionId;
    public AuctionRegistration() { }
    public AuctionRegistration(Customer customerUsername, Auction auctionId) {
        this.customerUsername = customerUsername;
        this.auctionId = auctionId;
    }
}
