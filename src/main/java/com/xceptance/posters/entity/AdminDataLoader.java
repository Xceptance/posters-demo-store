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
 * On upgrade from old module IDs (pre-Security rename), re-seeds role_modules entries.
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
            // Migration guard: if no role has the 'security' module ID, re-seed role_modules
            boolean needsMigration = roleRepository.findAll().stream()
                    .noneMatch(r -> r.getModuleIds().contains("security"));
            if (needsMigration) {
                log.info("Detected old module IDs (pre-hierarchy rename). Re-seeding role_modules...");
                reseedRoleModules();
            } else {
                log.info("Roles already exist with current module IDs, skipping seed.");
            }
            return;
        }

        createRoles();
        log.info("Seeded 4 default roles: Admin, Super User, Catalog User, Order User");
    }

    private void createRoles() {
        // All top-level module IDs
        Set<String> allModuleIds = Arrays.stream(BackofficeModule.values())
                .filter(BackofficeModule::isTopLevel)
                .map(BackofficeModule::getId)
                .collect(Collectors.toSet());

        // Admin — full access to all modules including Security
        createRole("Admin", "Full access to all modules including security administration", allModuleIds);

        // Super User — everything except Security
        Set<String> superUserModules = allModuleIds.stream()
                .filter(id -> !id.equals("security"))
                .collect(Collectors.toSet());
        createRole("Super User", "Access to all modules except security administration", superUserModules);

        // Catalog User — dashboard + catalog
        createRole("Catalog User", "Access to product and catalog management",
                Set.of("dashboard", "catalog"));

        // Order User — dashboard + orders
        createRole("Order User", "Access to order management",
                Set.of("dashboard", "orders"));
    }

    private void reseedRoleModules() {
        // Clear all role_modules entries and re-seed with updated module IDs
        roleRepository.findAll().forEach(role -> {
            switch (role.getName()) {
                case "Admin" -> {
                    Set<String> all = Arrays.stream(BackofficeModule.values())
                            .filter(BackofficeModule::isTopLevel)
                            .map(BackofficeModule::getId)
                            .collect(Collectors.toSet());
                    role.setModuleIds(all);
                }
                case "Super User" -> role.setModuleIds(
                        Set.of("dashboard", "catalog", "customers", "orders"));
                case "Catalog User" -> role.setModuleIds(
                        Set.of("dashboard", "catalog"));
                case "Order User" -> role.setModuleIds(
                        Set.of("dashboard", "orders"));
                default -> log.warn("Unknown role '{}' during migration, clearing module IDs", role.getName());
            }
            roleRepository.save(role);
        });
        log.info("Re-seeded role_modules with updated module IDs");
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
