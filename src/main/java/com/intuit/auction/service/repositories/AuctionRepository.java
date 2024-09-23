package com.intuit.auction.service.repositories;

import java.time.LocalDateTime;
import java.util.List;

import com.intuit.auction.service.entity.Auction;
import com.intuit.auction.service.enums.AuctionStatus;
import com.intuit.auction.service.enums.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuctionRepository extends JpaRepository<Auction, String> {

    @Query("SELECT a FROM auction a WHERE a.product.productCategory IN :categories OR a.auctionStatus IN :statuses")
    List<Auction> findByProductCategoryInAndAuctionStatusIn(@Param("categories") List<ProductCategory> productCategories,
                                                            @Param("statuses") List<AuctionStatus> auctionStatuses);

    @Query("SELECT COUNT(a) > 0 FROM auction a WHERE a.vendor.username = :vendorUsername AND a.auctionId = :auctionId")
    boolean checkAuctionByVendorUsername(@Param("vendorUsername") String vendorUsername,
                                         @Param("auctionId") String auctionId);

    @Modifying
    @Query("UPDATE auction a SET a.auctionStatus = :status WHERE a.auctionId = :auctionId")
    void updateAuctionStatusByAuctionId(@Param("status") AuctionStatus status,
                                        @Param("auctionId") String auctionId);

    @Query("SELECT a FROM auction a WHERE a.endTime < :now AND a.auctionStatus != :auctionStatus")
    List<Auction> findByEndTimeBeforeAndStatusNot(@Param("now") LocalDateTime now,
                                                  @Param("auctionStatus") AuctionStatus auctionStatus
    );

    List<Auction> findByAuctionStatus(AuctionStatus auctionStatus);
}
