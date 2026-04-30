## 1. Update Module Registry

- [ ] 1.1 Revise `BackofficeModule` enum to define the new hierarchy:
  - Top-level: Dashboard, Security, Catalog, Customers, Orders
  - Security submodules: security-users, security-audit-log, security-roles, security-import-export, security-settings
  - Catalog submodules: catalog-dashboard, catalog-categories, catalog-products, catalog-variations, catalog-pricing, catalog-import-export, catalog-settings
  - Customers submodules: customers-dashboard, customers-list, customers-import-export, customers-settings
  - Orders submodules: orders-dashboard, orders-list, orders-export
  - Update URL prefixes: `/backoffice/security/**`, `/backoffice/catalog/**`, `/backoffice/customers/**`, `/backoffice/orders/**`
- [ ] 1.2 Verify build compiles with new enum values

## 2. Update Role Seeding

- [ ] 2.1 Update `AdminDataLoader.seedRoles()` to use new top-level module IDs:
  - Admin: dashboard, security, catalog, customers, orders
  - Super User: dashboard, catalog, customers, orders
  - Catalog User: dashboard, catalog
  - Order User: dashboard, orders
- [ ] 2.2 Add migration guard: if no role contains module ID `security`, clear and re-seed all `role_modules` entries (preserving role names/descriptions)
- [ ] 2.3 Verify startup logs show correct re-seed or skip

## 3. Sidebar Fragment

- [ ] 3.1 Make `SidebarModelAdvice` a proper `@ControllerAdvice` scoped to backoffice controllers with a `@ModelAttribute("sidebarModules")` method that:
  - Reads the authenticated user's `AdminUserPrincipal.getPermittedModuleIds()`
  - Always includes Dashboard
  - Filters top-level modules to those whose ID is in the permitted set
  - For each permitted top-level module, includes all its submodules
  - Sets `active` and `expanded` flags by matching current request URL to module URL prefixes
- [ ] 3.2 Create `backoffice/layout/sidebar.html` — a Thymeleaf fragment (`th:fragment="sidebar"`) that:
  - Iterates `${sidebarModules}` via `th:each`
  - Renders Dashboard as a simple link
  - Renders modules with submodules as Bootstrap 5 collapse groups
  - Highlights active submodule, expands active parent group
  - Uses Material Symbols icons for each module
- [ ] 3.3 Update `default.html` to replace hardcoded sidebar `<ul>` with `th:replace="~{backoffice/layout/sidebar :: sidebar}"`
- [ ] 3.4 Verify sidebar renders dynamically and shows correct modules for the admin user

## 4. Update Controller URL Mappings

- [ ] 4.1 Update `AdminUserController`, `RoleController`, `AuditLogController`, `SecuritySettingsController` `@RequestMapping` from `/backoffice/admin/**` to `/backoffice/security/**`
- [ ] 4.2 Update `ModuleAccessInterceptor` URL prefix checks if needed
- [ ] 4.3 Update all template `th:action` and `th:href` links that reference old `/backoffice/admin/` paths
- [ ] 4.4 Verify audit log, users, roles, security pages still load under new paths

## 5. Stub Endpoints for New Submodules

- [ ] 5.1 Create stub controller endpoints (or add to existing controllers) for all unimplemented submodules:
  - Catalog: Dashboard, Categories, Products, Variations, Pricing, Import/Export, Settings
  - Customers: Dashboard, Customers list, Import/Export, Settings
  - Orders: Dashboard, Orders list, Export
- [ ] 5.2 Create or reuse a shared `backoffice/placeholder.html` template for "Coming Soon" pages
- [ ] 5.3 Verify all sidebar links navigate to a page without 404

## 6. Sidebar CSS

- [ ] 6.1 Add/update `backoffice.css`:
  - Collapsible group styles (chevron indicator, expand/collapse transition)
  - Active submodule highlight
  - Submodule indentation relative to parent
  - Parent group visual indicator when a submodule is active

## 7. Final Verification

- [ ] 7.1 Full build verification (`mvn clean package`)
- [ ] 7.2 Manual walkthrough:
  - Login as admin → sidebar shows all 5 top-level modules with submodule groups
  - Security group expands to show Users, Audit Log, Roles, Import/Export, Settings
  - Navigate to /backoffice/security/users → Users submodule is highlighted, Security group expanded
  - All stub submodule links navigate without 404
  - Roles page shows updated module display names for the new IDs
