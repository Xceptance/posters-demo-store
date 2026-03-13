package com.xceptance.posters.entity;

import com.xceptance.posters.config.BackofficeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Seeds default roles and admin user on startup if none exist.
 * Idempotent — does nothing if data already exists.
 */
@Component
public class AdminDataLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminDataLoader.class);

    private final AdminUserRepository adminUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminDataLoader(AdminUserRepository adminUserRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedRoles();
        seedAdminUser();
    }

    private void seedRoles() {
        if (roleRepository.count() > 0) {
            log.info("Roles already exist, skipping seed.");
            return;
        }

        // All module IDs for reference
        Set<String> allModuleIds = Arrays.stream(BackofficeModule.values())
                .filter(BackofficeModule::isTopLevel)
                .map(BackofficeModule::getId)
                .collect(Collectors.toSet());

        // Admin — full access to all modules including Admin
        createRole("Admin", "Full access to all modules including administration",
                allModuleIds);

        // Super User — everything except Admin module
        Set<String> superUserModules = allModuleIds.stream()
                .filter(id -> !id.equals("admin"))
                .collect(Collectors.toSet());
        createRole("Super User", "Access to all modules except administration",
                superUserModules);

        // Catalog User — dashboard + products + categories
        createRole("Catalog User", "Access to product and catalog management",
                Set.of("dashboard", "products", "categories"));

        // Order User — dashboard + orders + customers
        createRole("Order User", "Access to order management and customer data",
                Set.of("dashboard", "orders", "customers"));

        log.info("Seeded 4 default roles: Admin, Super User, Catalog User, Order User");
    }

    private Role createRole(String name, String description, Set<String> moduleIds) {
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        role.setBuiltIn(true);
        role.setModuleIds(moduleIds);
        return roleRepository.save(role);
    }

    private void seedAdminUser() {
        if (adminUserRepository.count() > 0) {
            log.info("Admin users already exist, skipping seed.");
            return;
        }

        Role adminRole = roleRepository.findByName("Admin")
                .orElseThrow(() -> new IllegalStateException("Admin role must be seeded before admin user"));

        AdminUser admin = new AdminUser();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin-2026!"));
        admin.setDisplayName("Administrator");
        admin.setRoles(Set.of(adminRole));
        adminUserRepository.save(admin);
        log.info("Seeded default admin user: admin (with Admin role)");
    }
}
