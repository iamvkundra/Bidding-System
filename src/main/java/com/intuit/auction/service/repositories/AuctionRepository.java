package com.intuit.auction.service.repositories;

import com.intuit.auction.service.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, String> {
}
