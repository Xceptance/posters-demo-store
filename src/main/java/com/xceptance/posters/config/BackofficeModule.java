package com.xceptance.posters.config;

import java.util.Arrays;
import java.util.List;

/**
 * Registry of all backoffice modules. Each top-level sidebar entry is a module;
 * submodules reference their parent. Access control is at the top-level module level only
 * (all-or-nothing: a role grants a top-level module AND all its submodules).
 */
public enum BackofficeModule {

    // --- Dashboard (no submodules) ---
    DASHBOARD("dashboard", "Dashboard", "dashboard", "/backoffice/", 0, null),

    // --- Security ---
    SECURITY("security", "Security", "admin_panel_settings", "/backoffice/security", 10, null),
    SECURITY_USERS("security-users", "Users", "person", "/backoffice/security/users", 11, SECURITY),
    SECURITY_AUDIT_LOG("security-audit-log", "Audit Log", "history", "/backoffice/security/audit-log", 12, SECURITY),
    SECURITY_ROLES("security-roles", "Roles", "shield_person", "/backoffice/security/roles", 13, SECURITY),
    SECURITY_IMPORT_EXPORT("security-import-export", "Import / Export", "sync_alt", "/backoffice/security/import-export", 14, SECURITY),
    SECURITY_SETTINGS("security-settings", "Settings", "settings", "/backoffice/security/settings", 15, SECURITY),

    // --- Catalog ---
    CATALOG("catalog", "Catalog", "inventory_2", "/backoffice/catalog", 20, null),
    CATALOG_DASHBOARD("catalog-dashboard", "Dashboard", "dashboard", "/backoffice/catalog/dashboard", 21, CATALOG),
    CATALOG_CATEGORIES("catalog-categories", "Categories", "category", "/backoffice/catalog/categories", 22, CATALOG),
    CATALOG_PRODUCTS("catalog-products", "Products", "deployed_code", "/backoffice/catalog/products", 23, CATALOG),
    CATALOG_VARIATIONS("catalog-variations", "Variations & Attributes", "tune", "/backoffice/catalog/variations", 24, CATALOG),
    CATALOG_PRICING("catalog-pricing", "Pricing", "sell", "/backoffice/catalog/pricing", 25, CATALOG),
    CATALOG_IMPORT_EXPORT("catalog-import-export", "Import / Export", "sync_alt", "/backoffice/catalog/import-export", 26, CATALOG),
    CATALOG_SETTINGS("catalog-settings", "Settings", "settings", "/backoffice/catalog/settings", 27, CATALOG),

    // --- Customers ---
    CUSTOMERS("customers", "Customers", "group", "/backoffice/customers", 30, null),
    CUSTOMERS_DASHBOARD("customers-dashboard", "Dashboard", "dashboard", "/backoffice/customers/dashboard", 31, CUSTOMERS),
    CUSTOMERS_LIST("customers-list", "Customers", "manage_accounts", "/backoffice/customers/list", 32, CUSTOMERS),
    CUSTOMERS_IMPORT_EXPORT("customers-import-export", "Import / Export", "sync_alt", "/backoffice/customers/import-export", 33, CUSTOMERS),
    CUSTOMERS_SETTINGS("customers-settings", "Settings", "settings", "/backoffice/customers/settings", 34, CUSTOMERS),

    // --- Orders ---
    ORDERS("orders", "Orders", "receipt_long", "/backoffice/orders", 40, null),
    ORDERS_DASHBOARD("orders-dashboard", "Dashboard", "dashboard", "/backoffice/orders/dashboard", 41, ORDERS),
    ORDERS_LIST("orders-list", "Orders", "list_alt", "/backoffice/orders/list", 42, ORDERS),
    ORDERS_EXPORT("orders-export", "Export", "download", "/backoffice/orders/export", 43, ORDERS);

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
     * Returns the submodules of this module, sorted by order.
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
     * Longest prefix match wins (so /backoffice/security/users beats /backoffice/security).
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
     * Returns the top-level module for access checking.
     * Submodules inherit access from their parent.
     */
    public BackofficeModule getAccessModule() {
        return parent != null ? parent : this;
    }
}
