## 1. Module Registry

- [ ] 1.1 Create `BackofficeModule` enum with fields: id, displayName, icon, urlPrefix, order, parentModule. Define all modules: Dashboard, Products, Categories, Customers, Orders, Settings, Admin, and Admin submodules (Users, Roles, Security Settings, Audit Log)
- [ ] 1.2 Verify build compiles

## 2. Role Entity and Data Model

- [ ] 2.1 Create `Role` entity with fields: id (Long, auto-increment), name (unique, not null), description (not null), builtIn (boolean, default true). Add `role_modules` join table mapping role_id to module string identifiers
- [ ] 2.2 Add `Set<Role>` many-to-many relationship to `AdminUser` entity with `admin_user_roles` join table
- [ ] 2.3 Create `RoleRepository` (Spring Data JPA)
- [ ] 2.4 Update `AdminDataLoader` to seed default roles (Admin, Super User, Catalog User, Order User) with their module mappings and assign Admin role to the default admin user
- [ ] 2.5 Verify build compiles and seeded data is correct on startup

## 3. Spring Security Route Protection

- [ ] 3.1 Create `ModuleAccessInterceptor` (HandlerInterceptor) that extracts the module from the URL prefix, checks against the authenticated user's permitted modules, and renders a "no permissions" error page on denial
- [ ] 3.2 Create `backoffice/error/access-denied.html` Thymeleaf template for the permission error page
- [ ] 3.3 Register the interceptor in `WebConfig` for `/backoffice/**` paths (excluding login)
- [ ] 3.4 Update `AdminUserDetailsService` to include role/module information in the authentication principal
- [ ] 3.5 Verify build and test that unauthorized module access returns the error page

## 4. Dynamic Sidebar Navigation

- [ ] 4.1 Create `SidebarModelAdvice` (`@ControllerAdvice` / `@ModelAttribute`) that computes the current user's visible modules (union of all role permissions), structures them as a parent/child hierarchy, and adds them to the model
- [ ] 4.2 Refactor `default.html` sidebar from static HTML to a Thymeleaf loop over the module model. Implement collapsible submodule groups using Bootstrap 5 collapse component
- [ ] 4.3 Add CSS for collapsible sidebar groups, active state highlighting, and submodule indentation
- [ ] 4.4 Compute active module/submodule server-side by matching the current request URL to module URL prefixes
- [ ] 4.5 Verify build and test sidebar renders correctly for different role combinations

## 5. Admin Module — Users Submodule

- [ ] 5.1 Create `AdminUserController` with endpoints for user list (GET `/backoffice/admin/users`), create form (GET/POST), edit form (GET/POST), delete (POST), and password reset (POST)
- [ ] 5.2 Create `AdminUserService` with methods for CRUD, role assignment, password reset, self-deletion prevention, and last-admin protection logic
- [ ] 5.3 Create `backoffice/admin/users/list.html` template with paginated table (username, display name, roles, created date), search bar, role filter dropdown, and pagination controls
- [ ] 5.4 Create `backoffice/admin/users/form.html` template for create/edit with fields for username, display name, password (create only), and role picker (checkbox list)
- [ ] 5.5 Create `backoffice/admin/users/reset-password.html` template or modal for password reset (new password + confirmation)
- [ ] 5.6 Implement self-deletion prevention: hide delete button for the logged-in user's own record
- [ ] 5.7 Implement last-admin protection: reject deletion or Admin role removal when only one Admin-role user remains
- [ ] 5.8 Verify build and test user CRUD, role assignment, password reset, and safety guardrails

## 6. Admin Module — Roles Submodule

- [ ] 6.1 Create `RoleController` with endpoint for roles list (GET `/backoffice/admin/roles`)
- [ ] 6.2 Create `backoffice/admin/roles/list.html` template displaying all roles with name, description, and permitted modules. No CRUD actions
- [ ] 6.3 Verify build and test roles list renders correctly

## 7. Admin Module — Audit Log

- [ ] 7.1 Create `AuditLogEntry` entity with fields: id, timestamp, userId, username, action (enum), targetType, targetId, details
- [ ] 7.2 Create `AuditLogRepository` with methods for paginated search by username, action type, and free-text details
- [ ] 7.3 Create `AuditLogService` with `log(...)` method for recording audit entries. Add explicit log calls to user CRUD operations, role assignments, password resets, and login/logout events
- [ ] 7.4 Create `AuditLogController` with endpoint for audit log view (GET `/backoffice/admin/audit-log`) with search and pagination parameters
- [ ] 7.5 Create `backoffice/admin/audit-log/list.html` template with paginated table, search field, and action type filter
- [ ] 7.6 Create `AuditLogCleanupTask` with `@Scheduled` method that runs daily and deletes entries older than 180 days
- [ ] 7.7 Verify build and test audit logging, search, and cleanup

## 8. Admin Module — Security Settings (Placeholder)

- [ ] 8.1 Create `SecuritySettingsController` with GET endpoint for `/backoffice/admin/security`
- [ ] 8.2 Create `backoffice/admin/security/index.html` placeholder template with "Coming Soon" message
- [ ] 8.3 Verify build

## 9. Final Verification

- [ ] 9.1 Full build verification (`mvn clean package` or equivalent)
- [ ] 9.2 Manual walkthrough: login as admin, verify sidebar shows all modules with collapsible Admin group, test user CRUD with role assignment, verify audit log captures actions, test permission denial for unauthorized module access
