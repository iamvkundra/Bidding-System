package com.intuit.auction.service.repositories;

import com.intuit.auction.service.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, String> {
}
