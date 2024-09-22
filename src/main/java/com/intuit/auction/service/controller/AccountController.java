package com.intuit.auction.service.controller;

import static com.intuit.auction.service.commons.Constants.ACCOUNT_SERVICE;

import java.security.Principal;
import javax.naming.AuthenticationException;

import com.intuit.auction.service.dto.AccountRequest;
import com.intuit.auction.service.dto.AccountResponseDto;
import com.intuit.auction.service.dto.LoginRequest;
import com.intuit.auction.service.dto.LoginResponseDto;
import com.intuit.auction.service.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ACCOUNT_SERVICE)
public class AccountController {

    @Autowired
    private AccountService accountService;


    @PostMapping("/register")
    @Operation(summary = "Create an account",
            description = "Create an account(VENDOR or CUSTOMER)")
    public ResponseEntity<?> createAccount(@RequestBody @Valid AccountRequest accountRequest) throws Exception {
        AccountResponseDto accountResponse = accountService.createAccount(accountRequest);
        return new ResponseEntity<>(accountResponse, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get an account details",
            description = "Get an account details by username of account")
    @PreAuthorize("hasAuthority('VENDOR') or hasAuthority('CUSTOMER')")
    public ResponseEntity<AccountResponseDto> getUserByUsername(Principal principal) {
        return new ResponseEntity<>(accountService.getUserByUsername(principal.getName()), HttpStatus.OK);
    }

    @PostMapping("/login")
    @Operation(summary = "Login API",
            description = "login to the application using username and password")
    public ResponseEntity<LoginResponseDto> authenticateAndGetToken(@RequestBody LoginRequest loginRequest) throws AuthenticationException {
        LoginResponseDto loginResponseDto = accountService.login(loginRequest);
        return new ResponseEntity<>(loginResponseDto, HttpStatus.OK);
    }
}
