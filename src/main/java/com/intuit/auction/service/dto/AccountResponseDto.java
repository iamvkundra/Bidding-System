package com.intuit.auction.service.dto;

import com.intuit.auction.service.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Data
@Getter
public class AccountResponseDto {
    private String username;
    private String name;
    private String email;
    private String number;
    private AccountType accountType;
}