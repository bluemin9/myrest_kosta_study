package com.blueming.myrest.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    // 로그인 페이지에서 로그인 버튼 누를 때 호출됨
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Optional 객체 (findByEmail)
        Account account = accountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        //return new User(account.getEmail(), account.getPassword(), authorities(account.getRoles()));
        return new AccountAdapter(account);

    }

    private Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
        // Set의 collection을 stream으로 받아온 후 mapping
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
    }

    public Account saveAccount(Account account) {
        account.setPassword(this.passwordEncoder.encode(account.getPassword()));
        return this.accountRepository.save(account);
    }

}
