package com.intuit.auction.service.controller;

import com.intuit.auction.service.dto.LoginRequest;
import com.intuit.auction.service.dto.LoginResponseDto;
import com.intuit.auction.service.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private Principal principal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");
    }

    @Test
    void authenticateAndGetToken_shouldReturnOkStatus() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        LoginResponseDto loginResponseDto = new LoginResponseDto();

        when(accountService.login(loginRequest)).thenReturn(loginResponseDto);

        ResponseEntity<LoginResponseDto> response = accountController.authenticateAndGetToken(loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(loginResponseDto);
        verify(accountService).login(loginRequest);
    }
}
