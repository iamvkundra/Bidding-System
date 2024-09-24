package com.intuit.auction.service.jobs;

import static com.intuit.auction.service.utils.CommonUtils.getNotificationRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.intuit.auction.service.entity.Auction;
import com.intuit.auction.service.entity.Bid;
import com.intuit.auction.service.enums.AuctionStatus;
import com.intuit.auction.service.enums.NotificationType;
import com.intuit.auction.service.notifications.NotificationManager;
import com.intuit.auction.service.notifications.dto.NotificationRequest;
import com.intuit.auction.service.repositories.AuctionRepository;
import com.intuit.auction.service.repositories.BidRepository;
import com.intuit.auction.service.utils.CommonUtils;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuctionManagement {

    private static final Logger log = LoggerFactory.getLogger(AuctionManagement.class);

    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private BidRepository bidRepository;
    @Autowired
    private NotificationManager notificationManager;

    private PriorityQueue<AuctionTimeEntry> scheduledAuctionsQueue;
    private PriorityQueue<AuctionTimeEntry> activeAuctionsQueue;

    private ScheduledExecutorService scheduledAuctionsExecutor;
    private ScheduledExecutorService activeAuctionsExecutor;

    public AuctionManagement() {
        this.scheduledAuctionsQueue = new PriorityQueue<>(Comparator.comparing(a -> a.actionTime));
        this.activeAuctionsQueue = new PriorityQueue<>(Comparator.comparing(a -> a.actionTime));

        this.scheduledAuctionsExecutor = Executors.newSingleThreadScheduledExecutor();
        this.activeAuctionsExecutor = Executors.newSingleThreadScheduledExecutor();
    }

    @PostConstruct
    public void initializeQueues() {
        LocalDateTime now = LocalDateTime.now();
        List<Auction> activeAuctions = auctionRepository.findByAuctionStatus(AuctionStatus.ACTIVE);
        for (Auction auction : activeAuctions) {
            if (auction.getEndTime().isAfter(now)) {
                activeAuctionsQueue.offer(new AuctionTimeEntry(auction.getAuctionId(), auction.getEndTime()));
            } else {
                closeAuction(auction);
            }
        }
        scheduleNextClosing();
    }

    public void addAuction(String auctionId, LocalDateTime auctionTime, AuctionStatus status) {
        if (status.equals(AuctionStatus.ACTIVE)) {
            activeAuctionsQueue.offer(new AuctionTimeEntry(auctionId, auctionTime));
            scheduleNextClosing();
        } else if (status.equals(AuctionStatus.SCHEDULED)) {
            scheduledAuctionsQueue.offer(new AuctionTimeEntry(auctionId, auctionTime));
            scheduleNextActivation();
        }
    }

    private void scheduleNextActivation() {
        scheduledAuctionsExecutor.shutdown();
        scheduledAuctionsExecutor = Executors.newSingleThreadScheduledExecutor();

        AuctionTimeEntry nextActivating = scheduledAuctionsQueue.peek();
        if (nextActivating != null) {
            long delay = java.time.Duration.between(LocalDateTime.now(), nextActivating.getActionTime()).toMillis();
            scheduledAuctionsExecutor.schedule(this:: processNextActivation, delay, TimeUnit.MILLISECONDS);
        }
    }

    private void scheduleNextClosing() {
        activeAuctionsExecutor.shutdown();
        activeAuctionsExecutor = Executors.newSingleThreadScheduledExecutor();

        AuctionTimeEntry nextClosing = activeAuctionsQueue.peek();
        if (nextClosing != null) {
            long delay = java.time.Duration.between(LocalDateTime.now(), nextClosing.getActionTime()).toMillis();
            activeAuctionsExecutor.schedule(this::processNextClosing, delay, TimeUnit.MILLISECONDS);
        }
    }

    private void processNextActivation() {
        LocalDateTime now = LocalDateTime.now();
        AuctionTimeEntry entry = scheduledAuctionsQueue.poll();

        if (entry != null) {
            if (entry.getActionTime().isAfter(now)) {
                scheduledAuctionsQueue.offer(entry);
            } else {
                Auction auction = auctionRepository.findById(entry.auctionId).orElse(null);
                if (auction != null && auction.getAuctionStatus() == AuctionStatus.SCHEDULED) {
                    activateAuction(auction);
                    activeAuctionsQueue.offer(new AuctionTimeEntry(auction.getAuctionId(), auction.getEndTime()));
                    scheduleNextClosing();
                }
            }
        }
        scheduleNextActivation();
    }

    private void processNextClosing() {
        LocalDateTime now = LocalDateTime.now();
        AuctionTimeEntry entry = activeAuctionsQueue.poll();

        if (entry != null) {
            if (entry.getActionTime().isAfter(now)) {
                activeAuctionsQueue.offer(entry);
            } else {
                Auction auction = auctionRepository.findById(entry.auctionId).orElse(null);
                if (auction != null && auction.getAuctionStatus() == AuctionStatus.ACTIVE) {
                    closeAuction(auction);
                }
            }
        }
        scheduleNextClosing();
    }

    private void activateAuction(Auction auction) {
        auction.setAuctionStatus(AuctionStatus.ACTIVE);
        auctionRepository.save(auction);
        log.info("Activated auction: {}", auction.getAuctionId());
    }

    private void closeAuction(Auction auction) {
        auction.setAuctionStatus(AuctionStatus.CLOSED);
        Bid winningBid = bidRepository.findHighestBidForAuction(auction);
        if (winningBid != null) {
            auction.setWinner(winningBid.getCustomer());
            auction.setWinningBid(winningBid);

            NotificationRequest notificationRequest = getNotificationRequest(auction);
            notificationManager.queueNotification(notificationRequest);
        }
        auctionRepository.save(auction);
        log.info("Closed auction: {}", auction.getAuctionId());
    }

    @Getter
    private static class AuctionTimeEntry {
        String auctionId;
        LocalDateTime actionTime;
        AuctionTimeEntry(String auctionId, LocalDateTime actionTime) {
            this.auctionId = auctionId;
            this.actionTime = actionTime;
        }
    }
}