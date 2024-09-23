package com.intuit.auction.service.dto;

import com.intuit.auction.service.enums.AccountType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

class AccountRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validAccountRequest_shouldNotHaveViolations() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAccountType(AccountType.VENDOR);
        accountRequest.setName("John Doe");
        accountRequest.setEmail("john.doe@example.com");
        accountRequest.setMobileNumber("1234567890");
        accountRequest.setUsername("johndoe");
        accountRequest.setPassword("securePassword");

        Set<ConstraintViolation<AccountRequest>> violations = validator.validate(accountRequest);
        assertThat(violations).isEmpty();
    }


    @Test
    void missingEmail_shouldHaveViolations() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAccountType(AccountType.CUSTOMER);
        accountRequest.setName("John Doe");
        accountRequest.setMobileNumber("1234567890");
        accountRequest.setUsername("johndoe");
        accountRequest.setPassword("securePassword");

        Set<ConstraintViolation<AccountRequest>> violations = validator.validate(accountRequest);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Email is required");
    }

    @Test
    void invalidEmail_shouldHaveViolations() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAccountType(AccountType.CUSTOMER);
        accountRequest.setName("John Doe");
        accountRequest.setEmail("invalid-email");
        accountRequest.setMobileNumber("1234567890");
        accountRequest.setUsername("johndoe");
        accountRequest.setPassword("securePassword");

        Set<ConstraintViolation<AccountRequest>> violations = validator.validate(accountRequest);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Email should be valid");
    }

    @Test
    void missingMobileNumber_shouldHaveViolations() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAccountType(AccountType.CUSTOMER);
        accountRequest.setName("John Doe");
        accountRequest.setEmail("john.doe@example.com");
        accountRequest.setUsername("johndoe");
        accountRequest.setPassword("securePassword");

        Set<ConstraintViolation<AccountRequest>> violations = validator.validate(accountRequest);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Mobile Number is required");
    }

    @Test
    void invalidMobileNumber_shouldHaveViolations() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAccountType(AccountType.CUSTOMER);
        accountRequest.setName("John Doe");
        accountRequest.setEmail("john.doe@example.com");
        accountRequest.setMobileNumber("12345"); // Invalid mobile number
        accountRequest.setUsername("johndoe");
        accountRequest.setPassword("securePassword");

        Set<ConstraintViolation<AccountRequest>> violations = validator.validate(accountRequest);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Mobile number must be 10 digits");
    }

    @Test
    void missingUsername_shouldHaveViolations() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAccountType(AccountType.CUSTOMER);
        accountRequest.setName("John Doe");
        accountRequest.setEmail("john.doe@example.com");
        accountRequest.setMobileNumber("1234567890");
        accountRequest.setPassword("securePassword");

        Set<ConstraintViolation<AccountRequest>> violations = validator.validate(accountRequest);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Username is required");
    }

    @Test
    void missingPassword_shouldHaveViolations() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setAccountType(AccountType.CUSTOMER);
        accountRequest.setName("John Doe");
        accountRequest.setEmail("john.doe@example.com");
        accountRequest.setMobileNumber("1234567890");
        accountRequest.setUsername("johndoe");

        Set<ConstraintViolation<AccountRequest>> violations = validator.validate(accountRequest);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Password is required");
    }
}
