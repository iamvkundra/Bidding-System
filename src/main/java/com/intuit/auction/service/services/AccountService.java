package com.intuit.auction.service.services;

import java.util.Optional;

import com.intuit.auction.service.entity.account.Account;
import com.intuit.auction.service.dto.AccountRequest;
import com.intuit.auction.service.dto.AccountResponseDto;

import com.intuit.auction.service.entity.account.Customer;
import com.intuit.auction.service.entity.account.Vendor;
import com.intuit.auction.service.entity.account.User;
import com.intuit.auction.service.enums.AccountType;
import com.intuit.auction.service.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountResponseDto createAccount(AccountRequest request) throws Exception {
        Account account = null;
        User user = createUser(request);
        switch (request.getAccountType()) {
            case VENDOR:
                account = new Vendor(request.getUsername(), request.getPassword(), user);
                break;
            case CUSTOMER:
                account = new Customer(request.getUsername(), request.getPassword(), user);
                break;
            default:
                break;
        }
        accountRepository.save(account);
        return null;
    }

    public AccountResponseDto getUserByUsername(String username) {
            Optional<Account> account = accountRepository.findById(username);
            return createAccountResponse(account.get());
    }

    public Account getAccount(String username) {
        Optional<Account> account = accountRepository.findById(username);
        return account.isPresent() ? account.get() : null;
    }

    private User createUser(AccountRequest accountRequest) {
        return new User(accountRequest.getName(), accountRequest.getEmail(), accountRequest.getMobileNumber());
    }

    public boolean isLoggedIn(String username) {
        return true;
    }

    private AccountResponseDto createAccountResponse(Account account) {
        AccountResponseDto accountResponseDto = new AccountResponseDto(account.getUsername(),
                account.getUser().getName(), account.getUser().getEmail(),
                account.getUser().getMobileNumber(), null);
        if (account instanceof Vendor) {
            accountResponseDto.setAccountType(AccountType.VENDOR);
        } else if (account instanceof  Customer) {
            accountResponseDto.setAccountType(AccountType.CUSTOMER);
        }
        return accountResponseDto;
    }
}
