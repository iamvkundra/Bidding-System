package com.intuit.auction.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Data
@Getter
public class AccountResponse {
    private String username;
    private String name;
    private String email;
    private String number;
}
