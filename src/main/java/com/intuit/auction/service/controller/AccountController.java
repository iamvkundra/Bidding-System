package com.intuit.auction.service.controller;

import static com.intuit.auction.service.commons.Constants.ACCOUNT_SERVICE;

import java.security.Principal;
import javax.naming.AuthenticationException;

import com.intuit.auction.service.dto.AccountRequest;
import com.intuit.auction.service.dto.AccountResponseDto;
import com.intuit.auction.service.dto.LoginRequest;
import com.intuit.auction.service.dto.LoginResponseDto;
import com.intuit.auction.service.exceptions.AccountException;
import com.intuit.auction.service.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public ResponseEntity<?> createAccount(@RequestBody @Valid AccountRequest accountRequest) {
        try {
            AccountResponseDto accountResponse = accountService.createAccount(accountRequest);
            return new ResponseEntity<>(accountResponse, HttpStatus.CREATED);
        } catch (AccountException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception exception) {
            return new ResponseEntity<>("An unexpected error occurred: "+ exception.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    @Operation(summary = "Get an account details",
            description = "Get an account details by username of account")
    @PreAuthorize("isAuthenticated() and (hasAuthority('VENDOR') or hasAuthority('CUSTOMER'))")
    public ResponseEntity<?> getUserByUsername(Principal principal) {
        try {
            return new ResponseEntity<>(accountService.getUserByUsername(principal.getName()), HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login API",
            description = "login to the application using username and password")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponseDto loginResponseDto = accountService.login(loginRequest);
            return new ResponseEntity<>(loginResponseDto, HttpStatus.OK);
        }catch (UsernameNotFoundException | AuthenticationException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (AccountException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
