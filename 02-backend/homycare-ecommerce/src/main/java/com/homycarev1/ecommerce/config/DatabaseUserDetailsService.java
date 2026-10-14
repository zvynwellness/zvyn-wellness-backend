package com.homycarev1.ecommerce.config;

import com.homycarev1.ecommerce.dao.AppUserRepository;
import com.homycarev1.ecommerce.entity.AppUser;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final AppUserRepository users;

    public DatabaseUserDetailsService(AppUserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser account = users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));

        return User.withUsername(account.getEmail())
                .password(account.getPasswordHash())
                .roles(account.getRole().name())
                .disabled(!account.isActive())
                .build();
    }
}
