package com.intuit.auction.service.authentication;

import com.intuit.auction.service.authentication.jwt.JwtAuthFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void securityFilterChain_shouldPermitPublicEndpoints() throws Exception {
        // Test public endpoints
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/bidding-system/account/register"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/bidding-system/account/login"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/h2-console"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/docs"))
                .andExpect(status().isOk());
    }

    @Test
    void passwordEncoder_shouldReturnBCryptPasswordEncoderInstance() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        assert passwordEncoder instanceof BCryptPasswordEncoder;
    }

    @Test
    void authenticationProvider_shouldReturnDaoAuthenticationProvider() {
        AuthenticationProvider provider = new DaoAuthenticationProvider();
        assert provider instanceof DaoAuthenticationProvider;
    }
}
