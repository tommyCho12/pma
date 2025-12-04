package com.pma.security;

import com.pma.dao.IUserAccountRepository;
import com.pma.entities.Permission;
import com.pma.entities.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private IUserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Load user by email instead of username
        UserAccount userAccount = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Build authorities from permissions
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Convert each permission to a GrantedAuthority
        for (Permission permission : userAccount.getPermissions()) {
            authorities.add(new SimpleGrantedAuthority("PERMISSION_" + permission.getName()));
        }

        // If no permissions, user is implicitly a viewer (no special authorities
        // needed)

        // Return Spring Security UserDetails
        return User.builder()
                .username(userAccount.getEmail()) // Use email as username
                .password(userAccount.getPassword())
                .disabled(!userAccount.isEnabled())
                .authorities(authorities)
                .build();
    }
}
