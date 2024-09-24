package com.intuit.auction.service.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



import com.intuit.auction.service.dto.AccountRequest;
import com.intuit.auction.service.dto.AccountResponseDto;
import com.intuit.auction.service.dto.LoginRequest;
import com.intuit.auction.service.dto.LoginResponseDto;
import com.intuit.auction.service.enums.AccountType;
import com.intuit.auction.service.exceptions.AccountException;
import com.intuit.auction.service.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.naming.AuthenticationException;
import java.security.Principal;

class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @Mock
    private Principal principal;

    @InjectMocks
    private AccountController accountController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAccount_Success() {
        AccountRequest accountRequest = new AccountRequest();
        AccountResponseDto accountResponseDto = new AccountResponseDto("mayank","mayank","asa@gmail.com","9709874208", AccountType.CUSTOMER);

        when(accountService.createAccount(any(AccountRequest.class))).thenReturn(accountResponseDto);

        ResponseEntity<?> responseEntity = accountController.createAccount(accountRequest);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(accountResponseDto, responseEntity.getBody());
        verify(accountService, times(1)).createAccount(accountRequest);
    }

    @Test
    void testCreateAccount_AccountException() {
        AccountRequest accountRequest = new AccountRequest();
        when(accountService.createAccount(any(AccountRequest.class))).thenThrow(new AccountException("Account creation failed"));

        ResponseEntity<?> responseEntity = accountController.createAccount(accountRequest);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Account creation failed", responseEntity.getBody());
        verify(accountService, times(1)).createAccount(accountRequest);
    }

    @Test
    void testCreateAccount_GenericException() {
        AccountRequest accountRequest = new AccountRequest();
        when(accountService.createAccount(any(AccountRequest.class))).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<?> responseEntity = accountController.createAccount(accountRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("An unexpected error occurred: Unexpected error", responseEntity.getBody());
        verify(accountService, times(1)).createAccount(accountRequest);
    }

    @Test
    void testGetUserByUsername_Success() {
        when(principal.getName()).thenReturn("username");
        AccountResponseDto accountResponseDto = new AccountResponseDto("mayank","mayank","asa@gmail.com","9709874208", AccountType.CUSTOMER);
        when(accountService.getUserByUsername("username")).thenReturn(accountResponseDto);

        ResponseEntity<?> responseEntity = accountController.getUserByUsername(principal);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(accountResponseDto, responseEntity.getBody());
        verify(accountService, times(1)).getUserByUsername("username");
    }

    @Test
    void testGetUserByUsername_GenericException() {
        when(principal.getName()).thenReturn("username");
        when(accountService.getUserByUsername("username")).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<?> responseEntity = accountController.getUserByUsername(principal);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Unexpected error", responseEntity.getBody());
        verify(accountService, times(1)).getUserByUsername("username");
    }

    @Test
    void testAuthenticateAndGetToken_Success() throws AuthenticationException {
        LoginRequest loginRequest = new LoginRequest();
        LoginResponseDto loginResponseDto = new LoginResponseDto();

        when(accountService.login(any(LoginRequest.class))).thenReturn(loginResponseDto);

        ResponseEntity<?> responseEntity = accountController.authenticateAndGetToken(loginRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(loginResponseDto, responseEntity.getBody());
        verify(accountService, times(1)).login(loginRequest);
    }

    @Test
    void testAuthenticateAndGetToken_UsernameNotFoundException() throws AuthenticationException {
        LoginRequest loginRequest = new LoginRequest();

        when(accountService.login(any(LoginRequest.class))).thenThrow(new UsernameNotFoundException("User not found"));

        ResponseEntity<?> responseEntity = accountController.authenticateAndGetToken(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals("User not found", responseEntity.getBody());
        verify(accountService, times(1)).login(loginRequest);
    }

    @Test
    void testAuthenticateAndGetToken_AuthenticationException() throws AuthenticationException {
        LoginRequest loginRequest = new LoginRequest();

        when(accountService.login(any(LoginRequest.class))).thenThrow(new AuthenticationException("Authentication failed"));

        ResponseEntity<?> responseEntity = accountController.authenticateAndGetToken(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals("Authentication failed", responseEntity.getBody());
        verify(accountService, times(1)).login(loginRequest);
    }

    @Test
    void testAuthenticateAndGetToken_AccountException() throws AuthenticationException {
        LoginRequest loginRequest = new LoginRequest();

        when(accountService.login(any(LoginRequest.class))).thenThrow(new AccountException("Account error"));

        ResponseEntity<?> responseEntity = accountController.authenticateAndGetToken(loginRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Account error", responseEntity.getBody());
        verify(accountService, times(1)).login(loginRequest);
    }
}
