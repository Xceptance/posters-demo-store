package com.xceptance.posters.service;

import com.xceptance.posters.config.BackofficeModule;
import com.xceptance.posters.repository.AdminUserRepository;
import com.xceptance.posters.entity.Role;
import com.xceptance.posters.repository.RoleRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service layer for role CRUD operations.
 */
@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final AdminUserRepository adminUserRepository;

    /** Map from module ID string to display name (all modules). */
    private static final Map<String, String> MODULE_DISPLAY_NAMES;
    /** Map from top-level module ID to display name (for role forms). */
    private static final Map<String, String> TOP_LEVEL_MODULE_NAMES;
    static {
        MODULE_DISPLAY_NAMES = new LinkedHashMap<>();
        Arrays.stream(BackofficeModule.values())
              .sorted((a, b) -> Integer.compare(a.getOrder(), b.getOrder()))
              .forEach(m -> MODULE_DISPLAY_NAMES.put(m.getId(), m.getDisplayName()));
        TOP_LEVEL_MODULE_NAMES = new LinkedHashMap<>();
        Arrays.stream(BackofficeModule.values())
              .filter(BackofficeModule::isTopLevel)
              .sorted((a, b) -> Integer.compare(a.getOrder(), b.getOrder()))
              .forEach(m -> TOP_LEVEL_MODULE_NAMES.put(m.getId(), m.getDisplayName()));
    }

    public RoleService(RoleRepository roleRepository, AdminUserRepository adminUserRepository) {
        this.roleRepository = roleRepository;
        this.adminUserRepository = adminUserRepository;
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    /**
     * Returns the number of users assigned to the given role.
     */
    public long countUsersForRole(Role role) {
        return adminUserRepository.countByRolesContaining(role);
    }

    /**
     * Returns a map of role ID → user count for all roles.
     */
    public Map<Long, Long> countUsersForAllRoles() {
        Map<Long, Long> counts = new LinkedHashMap<>();
        for (Role role : roleRepository.findAll()) {
            counts.put(role.getId(), countUsersForRole(role));
        }
        return counts;
    }

    /**
     * Returns true if the role can be deleted (not built-in and not assigned to any user).
     */
    public boolean isDeleteAllowed(Role role) {
        return !role.isBuiltIn() && countUsersForRole(role) == 0;
    }

    /**
     * Save a new or existing custom role.
     * @throws IllegalArgumentException if the role is built-in
     * @throws DataIntegrityViolationException if name is not unique
     */
    @Transactional
    public Role save(Role role) {
        if (role.getId() != null) {
            // Editing: verify the existing role is not built-in
            Role existing = roleRepository.findById(role.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + role.getId()));
            if (existing.isBuiltIn()) {
                throw new IllegalArgumentException("Built-in roles cannot be modified");
            }
        }
        role.setBuiltIn(false);
        return roleRepository.save(role);
    }

    /**
     * Delete a custom role.
     * @throws IllegalArgumentException if the role is built-in or assigned to users
     */
    @Transactional
    public void delete(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + id));
        if (role.isBuiltIn()) {
            throw new IllegalArgumentException("Built-in roles cannot be deleted");
        }
        if (countUsersForRole(role) > 0) {
            throw new IllegalArgumentException("Cannot delete role '" + role.getName()
                    + "' because it is assigned to " + countUsersForRole(role) + " user(s)");
        }
        roleRepository.delete(role);
    }

    /**
     * Returns the map of all module IDs to display names (for rendering badges).
     */
    public Map<String, String> getModuleDisplayNames() {
        return MODULE_DISPLAY_NAMES;
    }

    /**
     * Returns the map of top-level module IDs to display names (for role form checkboxes).
     */
    public Map<String, String> getTopLevelModuleDisplayNames() {
        return TOP_LEVEL_MODULE_NAMES;
    }
}
