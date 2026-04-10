package com.xceptance.posters.config;

import com.xceptance.posters.entity.AdminUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom UserDetails that carries the admin user's module access information.
 * Used by the sidebar and module access interceptor.
 */
public class AdminUserPrincipal implements UserDetails {

    private final AdminUser adminUser;
    private final Set<String> permittedModuleIds;

    public AdminUserPrincipal(AdminUser adminUser) {
        this.adminUser = adminUser;
        this.permittedModuleIds = adminUser.getRoles().stream()
                .flatMap(role -> role.getModuleIds().stream())
                .collect(Collectors.toSet());
    }

    public AdminUser getAdminUser() { return adminUser; }
    public Long getUserId() { return adminUser.getId(); }
    public String getDisplayName() { return adminUser.getDisplayName(); }
    public Set<String> getPermittedModuleIds() { return permittedModuleIds; }

    /**
     * Checks if the user has access to the given module.
     * Submodules inherit access from their parent.
     */
    public boolean hasModuleAccess(BackofficeModule module) {
        BackofficeModule accessModule = module.getAccessModule();
        return permittedModuleIds.contains(accessModule.getId());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Override
    public String getPassword() { return adminUser.getPassword(); }

    @Override
    public String getUsername() { return adminUser.getUsername(); }
}
