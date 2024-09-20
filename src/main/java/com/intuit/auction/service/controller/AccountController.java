package com.intuit.auction.service.controller;

import static com.intuit.auction.service.commons.Constants.ACCOUNT_SERVICE;

import com.intuit.auction.service.dto.AccountRequest;
import com.intuit.auction.service.dto.AccountResponse;
import com.intuit.auction.service.services.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ACCOUNT_SERVICE)
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody @Valid AccountRequest accountRequest) throws Exception {
        AccountResponse accountResponse = accountService.createAccount(accountRequest);
        return ResponseEntity.ok(accountResponse);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(accountService.getUserByUsername(username));
    }
}
