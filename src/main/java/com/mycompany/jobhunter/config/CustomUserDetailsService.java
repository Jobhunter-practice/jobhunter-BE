package com.mycompany.jobhunter.config;

import com.mycompany.jobhunter.service.contract.IUserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Component;

import java.util.Collections;

// override UserDetailsService by naming this component "userDetailsService"
@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    private final IUserService customUserDetailsService;

    public CustomUserDetailsService(IUserService userService) {
        this.customUserDetailsService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.mycompany.jobhunter.domain.entity.User user = this.customUserDetailsService.handleGetUserByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username/password is incorrect");
        }

        String role = user.getRole().getName();

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );

    }
}
