package com.intuit.auction.service.authentication.jwt;

import com.intuit.auction.service.authentication.UserInfoService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.io.IOException;

import static org.mockito.Mockito.*;

class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserInfoService userInfoService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext(); // Clear security context before each test
    }

    @Test
    void doFilterInternal_withValidToken_shouldAuthenticateUser() throws ServletException, IOException {
        String token = "validToken";
        String username = "testUser";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(userInfoService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.validateToken(token, userDetails)).thenReturn(true);
        when(userDetails.getAuthorities()).thenReturn(null);
        jwtAuthFilter.doFilterInternal(request, response, filterChain);
        verify(jwtService).extractUsername(token);
        verify(jwtService).validateToken(token, userDetails);
        verify(userInfoService).loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        assert SecurityContextHolder.getContext().getAuthentication() != null;
        verify(filterChain).doFilter(request, response); // Ensure filter chain proceeds
    }

    @Test
    void doFilterInternal_withInvalidToken_shouldNotAuthenticateUser() throws ServletException, IOException {
        // Arrange
        String token = "invalidToken";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(null); // Invalid token, no username extracted
        jwtAuthFilter.doFilterInternal(request, response, filterChain);
        verify(jwtService).extractUsername(token);
        verify(jwtService, never()).validateToken(anyString(), any(UserDetails.class));
        verify(userInfoService, never()).loadUserByUsername(anyString());

        assert SecurityContextHolder.getContext().getAuthentication() == null;
        verify(filterChain).doFilter(request, response); // Ensure filter chain proceeds
    }

    @Test
    void doFilterInternal_withoutAuthHeader_shouldNotAuthenticateUser() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null); // No auth header

        // Act
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtService, never()).extractUsername(anyString());
        verify(jwtService, never()).validateToken(anyString(), any(UserDetails.class));
        verify(userInfoService, never()).loadUserByUsername(anyString());

        assert SecurityContextHolder.getContext().getAuthentication() == null;
        verify(filterChain).doFilter(request, response); // Ensure filter chain proceeds
    }
}
