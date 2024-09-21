package com.intuit.auction.service.repositories;

import java.util.List;

import com.intuit.auction.service.entity.Auction;
import com.intuit.auction.service.entity.AuctionRegistration;
import com.intuit.auction.service.entity.account.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionRegistrationRepository extends JpaRepository<AuctionRegistration, String> {
    // Custom query for checking existence by user and auction
    @Query("SELECT CASE WHEN COUNT(ar) > 0 THEN true ELSE false END FROM AuctionRegistration ar WHERE ar.customerUsername = :customerUsername AND ar.auctionId = :auctionId")
    boolean existsByCustomerUsernameAndAuctionId(@Param("customerUsername") Customer customer, @Param("auctionId")
    Auction auctionId);
}
