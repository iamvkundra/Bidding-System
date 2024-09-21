package com.intuit.auction.service.dto;

import java.util.List;

import com.intuit.auction.service.enums.AuctionStatus;
import com.intuit.auction.service.enums.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuctionFilter {
    private List<ProductCategory> productCategory;
    private List<AuctionStatus> auctionStatuses;
}
