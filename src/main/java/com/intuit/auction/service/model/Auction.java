package com.intuit.auction.service.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.intuit.auction.service.model.Account.Customer;
import com.intuit.auction.service.model.Account.Vendor;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity(name = "auction")
@Data
public class Auction {

    @Id
    private String auctionId = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name = "vendor_username", referencedColumnName = "username")
    private Vendor vendor;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", referencedColumnName = "productId")
    private Product product;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name="customer_username", referencedColumnName = "username")
    private Customer highestBidder;

    private double highestBiddingPrice;
}
