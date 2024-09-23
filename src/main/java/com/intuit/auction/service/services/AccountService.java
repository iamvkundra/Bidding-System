package com.intuit.auction.service.services;

import java.util.Collections;
import java.util.Optional;

import javax.naming.AuthenticationException;

import com.intuit.auction.service.authentication.jwt.JwtService;
import com.intuit.auction.service.dto.LoginRequest;
import com.intuit.auction.service.dto.LoginResponseDto;
import com.intuit.auction.service.entity.account.Account;
import com.intuit.auction.service.dto.AccountRequest;
import com.intuit.auction.service.dto.AccountResponseDto;

import com.intuit.auction.service.entity.account.Customer;
import com.intuit.auction.service.entity.account.Vendor;
import com.intuit.auction.service.entity.account.User;
import com.intuit.auction.service.enums.AccountType;
import com.intuit.auction.service.exceptions.AccountException;
import com.intuit.auction.service.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Autowired
    public AccountService(AccountRepository accountRepository, JwtService jwtService,
                          AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public AccountResponseDto createAccount(AccountRequest request) {
        try {
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

            String hash = passwordEncoder.encode(account.getPassword());
            account.setPassword(hash);

            accountRepository.save(account);
            return createAccountResponse(account);
        } catch (DataIntegrityViolationException exception) {
            throw new AccountException("username-email exist: "+ exception.getMessage(), exception);
        } catch (Exception ex) {
            throw new AccountException("Failed to create account: " + ex.getMessage(), ex);
        }
    }

    public AccountResponseDto getUserByUsername(String username) {
        try {
            Optional<Account> account = accountRepository.findById(username);
            return createAccountResponse(account.get());
        } catch (AccountException exception) {
            throw new AccountException("Failed to get Account Details: "+ exception.getMessage(), exception);
        } catch (Exception exception) {
            throw new AccountException("Something went wrong" + exception.getMessage(), exception);
        }
    }

    public Account getAccount(String username) {
        Optional<Account> account = accountRepository.findById(username);
        return account.isPresent() ? account.get() : null;
    }

    private User createUser(AccountRequest accountRequest) {
        return new User(accountRequest.getName(), accountRequest.getEmail(), accountRequest.getMobileNumber());
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

    public LoginResponseDto login(LoginRequest loginRequest) throws AuthenticationException {
        try {
            LoginResponseDto loginResponseDto = new LoginResponseDto();

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            if (authentication.isAuthenticated()) {
                String authority = authentication.getAuthorities().stream()
                        .findFirst()
                        .map(GrantedAuthority::getAuthority)
                        .orElseThrow(() -> new UsernameNotFoundException("No authorities found!"));
                loginResponseDto.setUsername(loginRequest.getUsername());
                loginResponseDto.setToken(jwtService.generateToken(loginRequest.getUsername(), authority));


                loginResponseDto.setAccountType(AccountType.getAccountType(authority));
            } else {
                throw new UsernameNotFoundException("Invalid user request!");
            }
            return loginResponseDto;
        } catch (AccountException accountException) {
            throw new AccountException("Error while login: "+accountException.getMessage(), accountException);
        } catch (Exception exception) {
            throw new AccountException("Something went wrong: "+ exception.getMessage(), exception);
        }
    }

}
