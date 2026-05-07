package com.xceptance.posters.util;
import com.xceptance.posters.repository.RoleRepository;
import com.xceptance.posters.repository.AdminUserRepository;
import com.xceptance.posters.entity.Role;
import com.xceptance.posters.entity.AdminUser;

import com.xceptance.posters.config.BackofficeModule;
import com.xceptance.posters.config.BackofficeReadinessService;
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
    private final BackofficeReadinessService readinessService;

    public AdminDataLoader(AdminUserRepository adminUserRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           BackofficeReadinessService readinessService) {
        this.adminUserRepository = adminUserRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.readinessService = readinessService;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedRoles();
        seedAdminUser();
        readinessService.markReady();
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
        log.info("Seeded 5 default roles: System Admin, Business Admin, Customer Admin, Catalog User, Order User");
    }

    private void createRoles() {
        // All top-level module IDs
        Set<String> allModuleIds = Arrays.stream(BackofficeModule.values())
                .filter(BackofficeModule::isTopLevel)
                .map(BackofficeModule::getId)
                .collect(Collectors.toSet());

        // System Admin — full access to all modules including Security
        createRole("System Admin", "Full access to all modules including security administration", allModuleIds);

        // Business Admin — everything except Security
        Set<String> businessAdminModules = allModuleIds.stream()
                .filter(id -> !id.equals("security"))
                .collect(Collectors.toSet());
        createRole("Business Admin", "Access to all modules except security administration", businessAdminModules);

        // Catalog User — dashboard + catalog
        createRole("Catalog User", "Access to product and catalog management",
                Set.of("dashboard", "catalog"));

        // Order User — dashboard + orders
        createRole("Order User", "Access to order management",
                Set.of("dashboard", "orders"));

        // Customer Admin — customers only
        createRole("Customer Admin", "Access to customer management",
                Set.of("customers"));
    }

    private void reseedRoleModules() {
        // Clear all role_modules entries and re-seed with updated module IDs
        roleRepository.findAll().forEach(role -> {
            switch (role.getName()) {
                case "System Admin" -> {
                    Set<String> all = Arrays.stream(BackofficeModule.values())
                            .filter(BackofficeModule::isTopLevel)
                            .map(BackofficeModule::getId)
                            .collect(Collectors.toSet());
                    role.setModuleIds(all);
                }
                case "Business Admin" -> role.setModuleIds(
                        Set.of("dashboard", "catalog", "customers", "orders"));
                case "Catalog User" -> role.setModuleIds(
                        Set.of("dashboard", "catalog"));
                case "Order User" -> role.setModuleIds(
                        Set.of("dashboard", "orders"));
                case "Customer Admin" -> role.setModuleIds(
                        Set.of("customers"));
                default -> log.warn("Unknown role '{}' during migration, clearing module IDs", role.getName());
            }
            roleRepository.save(role);
        });

        if (roleRepository.findByName("Customer Admin").isEmpty()) {
            createRole("Customer Admin", "Access to customer management", Set.of("customers"));
            log.info("Created missing Customer Admin role during migration");
        }

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

        Role adminRole = roleRepository.findByName("System Admin")
                .orElseThrow(() -> new IllegalStateException("System Admin role must be seeded before admin user"));

        AdminUser admin = new AdminUser();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin-2026!"));
        admin.setDisplayName("Administrator");
        admin.setEmail("admin@posters-demo.local");
        admin.setRoles(Set.of(adminRole));
        adminUserRepository.save(admin);
        log.info("Seeded default admin user: admin (with System Admin role)");
    }
}
