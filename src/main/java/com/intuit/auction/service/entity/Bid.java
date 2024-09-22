package com.intuit.auction.service.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.intuit.auction.service.entity.account.Customer;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;

@Entity
@Table(name = "bid")
@Data
public class Bid {

    @Id
    private String bidId = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @ManyToOne
    @JoinColumn(name = "customer_username")
    private Customer customer;

    private double amount;

    @Getter
    private LocalDateTime bidTime;

    public Bid() { }
    public Bid(Auction auction, Customer customer, double amount) {
        this.auction = auction;
        this.customer = customer;
        this.amount = amount;
        this.bidTime = LocalDateTime.now();
    }
}
