package com.intuit.auction.service.authentication;

import com.intuit.auction.service.authentication.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        String username = "testUser";
        String role = "ROLE_USER";

        String token = jwtService.generateToken(username, role);

        assertThat(token).isNotNull();
        assertThat(jwtService.extractUsername(token)).isEqualTo(username);
        assertThat(jwtService.extractAllClaims(token).get("role")).isEqualTo(role);
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        String username = "testUser";
        String role = "ROLE_USER";
        String token = jwtService.generateToken(username, role);

        String extractedUsername = jwtService.extractUsername(token);

        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    void extractExpiration_shouldReturnCorrectExpiration() {
        String username = "testUser";
        String role = "ROLE_USER";
        String token = jwtService.generateToken(username, role);

        Date expirationDate = jwtService.extractExpiration(token);

        assertThat(expirationDate).isAfter(new Date());
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        String username = "testUser";
        String role = "ROLE_USER";
        String token = jwtService.generateToken(username, role);

        UserDetails userDetails = Mockito.mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);

        boolean isValid = jwtService.validateToken(token, userDetails);

        assertThat(isValid).isTrue();
    }

}
