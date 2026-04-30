package com.xceptance.posters.service;
import com.xceptance.posters.entity.AuditLogEntry;

import com.xceptance.posters.config.AdminUserPrincipal;
import com.xceptance.posters.entity.AdminUser;
import com.xceptance.posters.repository.AdminUserRepository;
import com.xceptance.posters.entity.AuditLogEntry.Action;
import com.xceptance.posters.entity.Role;
import com.xceptance.posters.repository.RoleRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for admin user management with safety guardrails
 * and audit logging of all mutating operations.
 */
@Service
public class AdminUserService {

    private final AdminUserRepository adminUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public AdminUserService(AdminUserRepository adminUserRepository,
                            RoleRepository roleRepository,
                            PasswordEncoder passwordEncoder,
                            AuditLogService auditLogService) {
        this.adminUserRepository = adminUserRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    public Page<AdminUser> findAll(String search, Long roleId, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            String q = "%" + search.toLowerCase() + "%";
            return adminUserRepository.findBySearch(q, pageable);
        }
        if (roleId != null) {
            return adminUserRepository.findByRolesId(roleId, pageable);
        }
        return adminUserRepository.findAll(pageable);
    }

    public Optional<AdminUser> findById(Long id) {
        return adminUserRepository.findById(id);
    }

    @Transactional
    public AdminUser createUser(String username, String displayName, String email, String password,
                                Set<Long> roleIds, AdminUserPrincipal caller) {
        AdminUser user = new AdminUser();
        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(resolveRoles(roleIds));
        AdminUser saved = adminUserRepository.save(user);

        auditLogService.log(
                caller.getUserId(), caller.getUsername(),
                Action.USER_CREATED,
                "AdminUser", saved.getId(),
                "Created user: " + username
        );
        return saved;
    }

    @Transactional
    public AdminUser updateUser(Long id, String username, String displayName, String email,
                                Set<Long> roleIds, AdminUserPrincipal caller) {
        AdminUser user = adminUserRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        Set<Role> newRoles = resolveRoles(roleIds);

        // Last-admin protection: if removing System Admin role from this user, check if they are the last
        Role adminRole = roleRepository.findByName("System Admin").orElse(null);
        if (adminRole != null && user.getRoles().contains(adminRole) && !newRoles.contains(adminRole)) {
            long adminCount = adminUserRepository.countByRolesContaining(adminRole);
            if (adminCount <= 1) {
                throw new IllegalStateException("Cannot remove System Admin role from the last admin user");
            }
        }

        String oldUsername = user.getUsername();
        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setEmail(email);
        user.setRoles(newRoles);
        AdminUser saved = adminUserRepository.save(user);

        auditLogService.log(
                caller.getUserId(), caller.getUsername(),
                Action.USER_UPDATED,
                "AdminUser", id,
                "Updated user: " + oldUsername + (oldUsername.equals(username) ? "" : " → " + username)
        );
        return saved;
    }

    @Transactional
    public void deleteUser(Long id, AdminUserPrincipal caller) {
        AdminUser user = adminUserRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        // Self-deletion prevention
        if (user.getId().equals(caller.getUserId())) {
            throw new IllegalStateException("You cannot delete your own account");
        }

        // Last-admin protection
        Role adminRole = roleRepository.findByName("System Admin").orElse(null);
        if (adminRole != null && user.getRoles().contains(adminRole)) {
            long adminCount = adminUserRepository.countByRolesContaining(adminRole);
            if (adminCount <= 1) {
                throw new IllegalStateException("Cannot delete the last admin user");
            }
        }

        String deletedUsername = user.getUsername();
        adminUserRepository.delete(user);

        auditLogService.log(
                caller.getUserId(), caller.getUsername(),
                Action.USER_DELETED,
                "AdminUser", id,
                "Deleted user: " + deletedUsername
        );
    }

    @Transactional
    public void resetPassword(Long id, AdminUserPrincipal caller) {
        throw new UnsupportedOperationException("Use resetPassword(id, newPassword, caller)");
    }

    @Transactional
    public void resetPassword(Long id, String newPassword, AdminUserPrincipal caller) {
        AdminUser user = adminUserRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        user.setPassword(passwordEncoder.encode(newPassword));
        adminUserRepository.save(user);

        auditLogService.log(
                caller.getUserId(), caller.getUsername(),
                Action.PASSWORD_RESET,
                "AdminUser", id,
                "Password reset for user: " + user.getUsername()
        );
    }

    private Set<Role> resolveRoles(Set<Long> roleIds) {
        return roleIds.stream()
                .map(roleRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }
}
