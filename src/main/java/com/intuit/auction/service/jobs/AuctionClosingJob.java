package com.intuit.auction.service.jobs;

import java.time.LocalDateTime;
import java.util.List;

import com.intuit.auction.service.entity.Auction;
import com.intuit.auction.service.entity.AuctionRegistration;
import com.intuit.auction.service.entity.Bid;
import com.intuit.auction.service.enums.AuctionStatus;
import com.intuit.auction.service.repositories.AuctionRepository;
import com.intuit.auction.service.repositories.BidRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AuctionClosingJob {

    private static final Logger log = LoggerFactory.getLogger(AuctionClosingJob.class);
    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private BidRepository bidRepository;

    @Scheduled(cron = "0 * * * * *")
    public void closeExpiredAuctions() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Triggered closeExpiredAuctions Job at Time: {}", now);
        List<Auction> expiredAuctions = auctionRepository.findByEndTimeBeforeAndStatusNot(now, AuctionStatus.CLOSED);
        for (Auction auction : expiredAuctions) {
            auction.setAuctionStatus(AuctionStatus.CLOSED);

            Bid winningBid = bidRepository.findHighestBidForAuction(auction);

            if (winningBid != null) {
                auction.setWinner(winningBid.getCustomer());
                auction.setWinningBid(winningBid);
                //Notification.
            }
            auctionRepository.save(auction);
        }
    }
}