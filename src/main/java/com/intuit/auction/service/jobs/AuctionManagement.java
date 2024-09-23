package com.intuit.auction.service.jobs;

import java.time.LocalDateTime;
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

    public void initializeQueues() {
        LocalDateTime now = LocalDateTime.now();

        // Handle scheduled auctions
//        List<Auction> scheduledAuctions = auctionRepository.findByAuctionStatus(AuctionStatus.SCHEDULED);
//        for (Auction auction : scheduledAuctions) {
//            if (auction.getStartTime().isAfter(now)) {
//                scheduledAuctionsQueue.offer(new AuctionTimeEntry(auction.getAuctionId(), auction.getStartTime()));
//            } else if (auction.getEndTime().isAfter(now)) {
//                activateAuction(auction);
//                activeAuctionsQueue.offer(new AuctionTimeEntry(auction.getAuctionId(), auction.getEndTime()));
//            } else {
//                closeAuction(auction);
//            }
//        }

        // Handle active auctions
        List<Auction> activeAuctions = auctionRepository.findByAuctionStatus(AuctionStatus.ACTIVE);
        for (Auction auction : activeAuctions) {
            if (auction.getEndTime().isAfter(now)) {
                activeAuctionsQueue.offer(new AuctionTimeEntry(auction.getAuctionId(), auction.getEndTime()));
                log.info("Adding into activeAuctionQueue: " + auction.getEndTime().isAfter(now) + " "+ CommonUtils.toJson(auction, false));
            } else {
                log.info("Closing info activeAuctionQueue");
                closeAuction(auction);
            }
        }

        scheduleNextActivation();
        scheduleNextClosing();
    }

    public void addAuction(String auctionId, LocalDateTime startTime, LocalDateTime endTime, AuctionStatus status) {
        LocalDateTime now = LocalDateTime.now();
        if (status == AuctionStatus.SCHEDULED && startTime.isAfter(now)) {
            scheduledAuctionsQueue.offer(new AuctionTimeEntry(auctionId, startTime));
            scheduleNextActivation();
        } else if ((status == AuctionStatus.ACTIVE || (status == AuctionStatus.SCHEDULED && startTime.isBefore(now))) && endTime.isAfter(now)) {
            activeAuctionsQueue.offer(new AuctionTimeEntry(auctionId, endTime));
            scheduleNextClosing();
        }
    }

    private void scheduleNextActivation() {
        AuctionTimeEntry nextActivation = scheduledAuctionsQueue.peek();
        if (nextActivation != null) {
            long delay = LocalDateTime.now().until(nextActivation.actionTime, java.time.temporal.ChronoUnit.MILLIS);
            scheduledAuctionsExecutor.schedule(this::processNextActivation, delay, TimeUnit.MILLISECONDS);
        }
    }

    private void scheduleNextClosing() {
        AuctionTimeEntry nextClosing = activeAuctionsQueue.peek();
        if (nextClosing != null) {
            long delay = LocalDateTime.now().until(nextClosing.actionTime, java.time.temporal.ChronoUnit.MILLIS);
            activeAuctionsExecutor.schedule(this::processNextClosing, delay, TimeUnit.MILLISECONDS);
        }
    }

    private void processNextActivation() {
        AuctionTimeEntry entry = scheduledAuctionsQueue.poll();
        if (entry != null) {
            Auction auction = auctionRepository.findById(entry.auctionId).orElse(null);
            if (auction != null && auction.getAuctionStatus() == AuctionStatus.SCHEDULED) {
                activateAuction(auction);
                activeAuctionsQueue.offer(new AuctionTimeEntry(auction.getAuctionId(), auction.getEndTime()));
                scheduleNextClosing();
            }
        }
        scheduleNextActivation();
    }

    private void processNextClosing() {
        AuctionTimeEntry entry = activeAuctionsQueue.poll();
        if (entry != null) {
            Auction auction = auctionRepository.findById(entry.auctionId).orElse(null);
            if (auction != null && auction.getAuctionStatus() == AuctionStatus.ACTIVE) {
                closeAuction(auction);
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

    private static NotificationRequest getNotificationRequest(Auction auction) {
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setNotificationType(NotificationType.EMAIL);
        notificationRequest.setRecipientEmail(auction.getWinner().getUser().getEmail());
        notificationRequest.setSubject("You have successfully won the auction: " + auction.getAuctionId());
        notificationRequest.setContent("Please contact vendor with given auctionId and product: " + auction.getProduct().getProductName());
        return notificationRequest;
    }

    private static class AuctionTimeEntry {
        String auctionId;
        LocalDateTime actionTime;

        AuctionTimeEntry(String auctionId, LocalDateTime actionTime) {
            this.auctionId = auctionId;
            this.actionTime = actionTime;
        }
    }
}