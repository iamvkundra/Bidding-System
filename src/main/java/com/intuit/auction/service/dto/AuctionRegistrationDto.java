package com.intuit.auction.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuctionRegistrationDto {

    @NotBlank(message = "Auction Id is Required")
    private String auctionId;
}
