package com.intuit.auction.service.dto;

import com.intuit.auction.service.enums.AuctionStatus;
import com.intuit.auction.service.enums.ProductCategory;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuctionFilterTest {

    @Test
    public void testAuctionFilter() {
        // Create sample data for the filter
        List<ProductCategory> categories = Arrays.asList(ProductCategory.ELECTRONIC, ProductCategory.FURNITURE);
        List<AuctionStatus> statuses = Arrays.asList(AuctionStatus.ACTIVE, AuctionStatus.CLOSED);

        // Create an instance of AuctionFilter
        AuctionFilter auctionFilter = new AuctionFilter(categories, statuses);

        // Validate the getters
        assertEquals(categories, auctionFilter.getProductCategory());
        assertEquals(statuses, auctionFilter.getAuctionStatuses());

        // Test the setters
        List<ProductCategory> newCategories = Arrays.asList(ProductCategory.CLOTHING);
        auctionFilter.setProductCategory(newCategories);
        assertEquals(newCategories, auctionFilter.getProductCategory());

        List<AuctionStatus> newStatuses = Arrays.asList(AuctionStatus.SCHEDULED);
        auctionFilter.setAuctionStatuses(newStatuses);
        assertEquals(newStatuses, auctionFilter.getAuctionStatuses());
    }
}
