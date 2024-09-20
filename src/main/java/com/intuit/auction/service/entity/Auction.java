package com.intuit.auction.service.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.intuit.auction.service.entity.account.Vendor;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity(name = "auction")
@Data
@AllArgsConstructor
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

    private double auctionBasePrice;
    private double highestBiddingPrice;

    public Auction() { }
    public Auction (Vendor vendor, Product product, LocalDateTime startTime, LocalDateTime endTime, double auctionBasePrice) {
        this.vendor = vendor;
        this.startTime= startTime;
        this.endTime = endTime;
        this.product = product;
        this.auctionBasePrice = auctionBasePrice;
    }
}
