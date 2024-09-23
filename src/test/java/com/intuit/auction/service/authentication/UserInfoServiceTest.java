package com.intuit.auction.service.authentication;

import com.intuit.auction.service.entity.account.Account;
import com.intuit.auction.service.repositories.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

class UserInfoServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserInfoService userInfoService;

    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        account = new Account();
        account.setUsername("testUser");
        account.setPassword("plainPassword");
    }

    @Test
    void loadUserByUsername_existingUser_shouldReturnUserDetails() {
        when(accountRepository.findById("testUser")).thenReturn(Optional.of(account));
        UserDetails userDetails = userInfoService.loadUserByUsername("testUser");
        assertThat(userDetails.getUsername()).isEqualTo("testUser");
    }

    @Test
    void loadUserByUsername_nonExistingUser_shouldThrowUsernameNotFoundException() {
        when(accountRepository.findById("nonExistentUser")).thenReturn(Optional.empty());

        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> userInfoService.loadUserByUsername("nonExistentUser"))
                .withMessage("User not found: nonExistentUser");
    }

    @Test
    void addUser_shouldEncodePasswordAndSaveUser() {
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        String result = userInfoService.addUser(account);
        assertThat(result).isEqualTo("User Added Successfully");
        assertThat(account.getPassword()).isEqualTo("encodedPassword"); // Check if the password is encoded
    }
}
