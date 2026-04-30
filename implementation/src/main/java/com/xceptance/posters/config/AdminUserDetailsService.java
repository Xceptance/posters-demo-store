package com.xceptance.posters.config;

import com.xceptance.posters.repository.AdminUserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Loads admin users from the admin_users table for Spring Security authentication.
 * Returns an AdminUserPrincipal that carries role/module access information.
 */
@Service
public class AdminUserDetailsService implements UserDetailsService {

    private final AdminUserRepository adminUserRepository;

    public AdminUserDetailsService(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var adminUser = adminUserRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Admin user not found: " + username));

        return new AdminUserPrincipal(adminUser);
    }
}
