package com.intuit.auction.service.authentication;

import java.util.Optional;

import com.intuit.auction.service.entity.account.Account;
import com.intuit.auction.service.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserInfoService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> userDetail = accountRepository.findById(username); // Assuming 'email' is used as username

        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public String addUser(Account account) {
        account.setPassword(encoder.encode(account.getPassword()));
        accountRepository.save(account);
        return "User Added Successfully";
    }
}
