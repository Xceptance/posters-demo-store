package com.xceptance.posters.service;

import com.xceptance.posters.config.AdminUserPrincipal;
import com.xceptance.posters.entity.AdminUser;
import com.xceptance.posters.entity.AdminUserRepository;
import com.xceptance.posters.entity.Role;
import com.xceptance.posters.entity.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for admin user management with safety guardrails.
 */
@Service
public class AdminUserService {

    private final AdminUserRepository adminUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(AdminUserRepository adminUserRepository,
                            RoleRepository roleRepository,
                            PasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<AdminUser> findAll(String search, Long roleId, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            String q = "%" + search.toLowerCase() + "%";
            return adminUserRepository.findBySearch(q, pageable);
        }
        return adminUserRepository.findAll(pageable);
    }

    public Optional<AdminUser> findById(Long id) {
        return adminUserRepository.findById(id);
    }

    @Transactional
    public AdminUser createUser(String username, String displayName, String password, Set<Long> roleIds) {
        AdminUser user = new AdminUser();
        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(resolveRoles(roleIds));
        return adminUserRepository.save(user);
    }

    @Transactional
    public AdminUser updateUser(Long id, String username, String displayName, Set<Long> roleIds,
                                AdminUserPrincipal currentPrincipal) {
        AdminUser user = adminUserRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        Set<Role> newRoles = resolveRoles(roleIds);

        // Last-admin protection: if removing Admin role from this user, check if they are the last
        Role adminRole = roleRepository.findByName("Admin").orElse(null);
        if (adminRole != null && user.getRoles().contains(adminRole) && !newRoles.contains(adminRole)) {
            long adminCount = adminUserRepository.countByRolesContaining(adminRole);
            if (adminCount <= 1) {
                throw new IllegalStateException("Cannot remove Admin role from the last admin user");
            }
        }

        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setRoles(newRoles);
        return adminUserRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id, AdminUserPrincipal currentPrincipal) {
        AdminUser user = adminUserRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        // Self-deletion prevention
        if (user.getId().equals(currentPrincipal.getUserId())) {
            throw new IllegalStateException("You cannot delete your own account");
        }

        // Last-admin protection
        Role adminRole = roleRepository.findByName("Admin").orElse(null);
        if (adminRole != null && user.getRoles().contains(adminRole)) {
            long adminCount = adminUserRepository.countByRolesContaining(adminRole);
            if (adminCount <= 1) {
                throw new IllegalStateException("Cannot delete the last admin user");
            }
        }

        adminUserRepository.delete(user);
    }

    @Transactional
    public void resetPassword(Long id, String newPassword) {
        AdminUser user = adminUserRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        user.setPassword(passwordEncoder.encode(newPassword));
        adminUserRepository.save(user);
    }

    private Set<Role> resolveRoles(Set<Long> roleIds) {
        return roleIds.stream()
                .map(roleRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }
}
