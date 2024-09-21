package com.intuit.auction.service.repositories;

import java.util.List;

import com.intuit.auction.service.entity.Auction;
import com.intuit.auction.service.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BidRepository extends JpaRepository<Bid, String> {

    @Query("SELECT b FROM Bid b WHERE b.customer.username = :customerUsername AND b.auction.auctionId = :auctionId")
    List<Bid> findByCustomer_UsernameAndAuction_AuctionId(@Param("customerUsername") String customerUsername,
                                                    @Param("auctionId") String auctionId);

    @Query("SELECT b FROM Bid b WHERE b.customer.username = :customerUsername")
    List<Bid> findByCustomer_Username(@Param("customerUsername") String customerUsername);

    @Query("SELECT b FROM Bid b WHERE b.auction.auctionId = :auctionId")
    List<Bid> findByAuctionId(@Param("auctionId") String auctionId);

    @Query("SELECT b FROM Bid b WHERE b.auction = :auction AND b.amount = (SELECT MAX(b2.amount) FROM Bid b2 WHERE b2.auction = :auction)")
    Bid findHighestBidForAuction(@Param("auction") Auction auction);
}
