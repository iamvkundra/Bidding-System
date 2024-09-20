package com.intuit.auction.service.services;

import java.util.Optional;

import com.intuit.auction.service.enums.AccountType;
import com.intuit.auction.service.model.Account.Account;
import com.intuit.auction.service.model.Account.AccountRequest;
import com.intuit.auction.service.model.Account.AccountResponse;

import com.intuit.auction.service.model.Account.Customer;
import com.intuit.auction.service.model.Account.Vendor;
import com.intuit.auction.service.model.User;
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

    public AccountResponse createAccount(AccountRequest request) throws Exception {
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

    public AccountResponse getUserByUsername(String username) {
        Optional<Account> account = accountRepository.findById(username);
        return account.map(this::createAccountResponse).orElse(null);
    }

    private User createUser(AccountRequest accountRequest) {
        return new User(accountRequest.getName(), accountRequest.getEmail(), accountRequest.getMobileNumber());
    }

    private AccountResponse createAccountResponse(Account account) {
        AccountResponse accountResponse = new AccountResponse(account.getUsername(), account.getUser().getName(),
                account.getUser().getEmail(),account.getUser().getMobileNumber());
        return accountResponse;
    }
}
