package com.intuit.auction.service.dto;

import com.intuit.auction.service.enums.AccountType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountResponseDtoTest {

    @Test
    public void testAccountResponseDto() {
        // Create an instance of AccountResponseDto
        AccountResponseDto accountResponseDto = new AccountResponseDto(
                "johndoe",
                "John Doe",
                "john.doe@example.com",
                "1234567890",
                AccountType.CUSTOMER
        );

        // Validate the getters
        assertEquals("johndoe", accountResponseDto.getUsername());
        assertEquals("John Doe", accountResponseDto.getName());
        assertEquals("john.doe@example.com", accountResponseDto.getEmail());
        assertEquals("1234567890", accountResponseDto.getNumber());
        assertEquals(AccountType.CUSTOMER, accountResponseDto.getAccountType());

        // Validate the setters
        accountResponseDto.setUsername("janedoe");
        assertEquals("janedoe", accountResponseDto.getUsername());

        accountResponseDto.setName("Jane Doe");
        assertEquals("Jane Doe", accountResponseDto.getName());

        accountResponseDto.setEmail("jane.doe@example.com");
        assertEquals("jane.doe@example.com", accountResponseDto.getEmail());

        accountResponseDto.setNumber("0987654321");
        assertEquals("0987654321", accountResponseDto.getNumber());

        accountResponseDto.setAccountType(AccountType.VENDOR);
        assertEquals(AccountType.VENDOR, accountResponseDto.getAccountType());
    }
}
