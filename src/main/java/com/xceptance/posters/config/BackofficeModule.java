package com.xceptance.posters.config;

import java.util.Arrays;
import java.util.List;

/**
 * Registry of all backoffice modules. Each top-level sidebar entry is a module;
 * submodules reference their parent. Access control is at the module level.
 */
public enum BackofficeModule {

    // Top-level modules
    DASHBOARD("dashboard", "Dashboard", "dashboard", "/backoffice/", 0, null),
    PRODUCTS("products", "Products", "inventory_2", "/backoffice/products", 10, null),
    CATEGORIES("categories", "Categories", "category", "/backoffice/categories", 20, null),
    CUSTOMERS("customers", "Customers", "group", "/backoffice/customers", 30, null),
    ORDERS("orders", "Orders", "receipt_long", "/backoffice/orders", 40, null),
    SETTINGS("settings", "Settings", "settings", "/backoffice/settings", 50, null),
    ADMIN("admin", "Admin", "admin_panel_settings", "/backoffice/admin", 60, null),

    // Admin submodules
    ADMIN_USERS("admin-users", "Users", "person", "/backoffice/admin/users", 61, ADMIN),
    ADMIN_ROLES("admin-roles", "Roles", "shield_person", "/backoffice/admin/roles", 62, ADMIN),
    ADMIN_SECURITY("admin-security", "Security Settings", "security", "/backoffice/admin/security", 63, ADMIN),
    ADMIN_AUDIT_LOG("admin-audit-log", "Audit Log", "history", "/backoffice/admin/audit-log", 64, ADMIN);

    private final String id;
    private final String displayName;
    private final String icon;
    private final String urlPrefix;
    private final int order;
    private final BackofficeModule parent;

    BackofficeModule(String id, String displayName, String icon, String urlPrefix, int order, BackofficeModule parent) {
        this.id = id;
        this.displayName = displayName;
        this.icon = icon;
        this.urlPrefix = urlPrefix;
        this.order = order;
        this.parent = parent;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getIcon() { return icon; }
    public String getUrlPrefix() { return urlPrefix; }
    public int getOrder() { return order; }
    public BackofficeModule getParent() { return parent; }

    public boolean isTopLevel() { return parent == null; }

    /**
     * Returns the submodules of this module.
     */
    public List<BackofficeModule> getSubmodules() {
        return Arrays.stream(values())
                .filter(m -> m.parent == this)
                .sorted((a, b) -> Integer.compare(a.order, b.order))
                .toList();
    }

    /**
     * Returns all top-level modules, sorted by order.
     */
    public static List<BackofficeModule> topLevelModules() {
        return Arrays.stream(values())
                .filter(BackofficeModule::isTopLevel)
                .sorted((a, b) -> Integer.compare(a.order, b.order))
                .toList();
    }

    /**
     * Finds the module whose URL prefix best matches the given path.
     * Longest prefix match wins (so /backoffice/admin/users beats /backoffice/admin).
     */
    public static BackofficeModule fromPath(String path) {
        BackofficeModule best = null;
        int bestLen = 0;
        for (BackofficeModule m : values()) {
            if (path.startsWith(m.urlPrefix) && m.urlPrefix.length() > bestLen) {
                best = m;
                bestLen = m.urlPrefix.length();
            }
        }
        return best;
    }

    /**
     * Returns the effective module for access checking.
     * Submodules inherit access from their parent.
     */
    public BackofficeModule getAccessModule() {
        return parent != null ? parent : this;
    }
}
