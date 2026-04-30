## Why

The backoffice currently has a flat user model — every admin can see and do everything. As the backoffice grows with more modules (Products, Orders, Customers, Settings, etc.), we need role-based access control so different admin users can be scoped to the modules they actually need. Additionally, the sidebar needs to evolve from a flat list to support hierarchical navigation with collapsible submodules.

## What Changes

- Introduce a **Role** entity with fields: `id`, `name` (unique), `description`, and a set of permitted modules. The Backoffice Admin module permission cannot be set or unset via the UI — it is reserved for the built-in Admin role
- Extend `AdminUser` with a many-to-many relationship to roles
- Define a **Module** registry — each top-level sidebar entry is a module; modules can contain submodules for deeper navigation
- Roles grant access at the **module level** only — no read/write distinction; if you can see it, you can work in it
- Submodules inherit access from their parent module (no separate role assignment)
- The sidebar dynamically renders only the modules (and their submodules) the current user has access to. Modules the user has no access to are hidden; directly navigating to a hidden module's URL returns a "no permissions" error page
- A dedicated **Admin** module serves as the hub for system administration, with four submodules:
  - **Users** — CRUD admin users, assign roles, reset passwords (admin sets a new password directly — temporary approach). Users are listed in a table with search, filtering, and pagination.
  - **Roles** — list, view, and assign roles. Default roles are seeded and immutable (cannot be edited or deleted, only assigned)
  - **Security Settings** — system-level security configuration
  - **Audit Log** — searchable log of all user activities, retained for 180 days
- **Default seeded roles** (immutable — can be assigned but not edited or deleted):
  - **Admin** — full access to all modules including Backoffice Admin
  - **Super User** — access to all modules except Backoffice Admin
  - **Catalog User** — access to product/catalog-related modules
  - **Order User** — access to order-related modules
  - More roles to come as modules are added
- **Only the Admin role grants access to the Backoffice Admin module** — user/role management and audit log are not available to other roles
- **A user cannot delete themselves** — the delete action is hidden/disabled for the currently logged-in user
- **The last Admin-role user cannot be deleted or demoted** — the system must always retain at least one user with the Admin role
- The seeded default `admin` user gets the Admin role

## Capabilities

### New Capabilities

- `backoffice-roles`: Role entity, module registry, role-to-module mapping. No role CRUD — default roles are seeded and immutable, only assignable to users
- `backoffice-navigation`: Dynamic sidebar rendering based on user roles, hierarchical module/submodule display with collapsible sections
- `backoffice-audit-log`: Audit log entity recording all user activities, searchable list view, automatic cleanup of entries older than 180 days

### Modified Capabilities

- `backoffice-users`: Add role assignments to admin users (many-to-many), extend user management screens with role picker, wire roles into Spring Security authorization
- `backoffice-layout`: Sidebar changes from static HTML to dynamic, role-filtered, hierarchical navigation with collapsible submodule groups

## Impact

- **Entities**: New `Role`, `Module` (or config-based registry), `AuditLogEntry`, and join tables (`admin_user_roles`, `role_modules`)
- **Spring Security**: `SecurityConfig` needs to enforce module-level access on backoffice routes (e.g., `/backoffice/products/**` requires a role that includes the Products module)
- **Sidebar template**: `default.html` sidebar becomes a Thymeleaf loop over the user's permitted modules with collapsible submodule groups
- **AdminUser entity**: Gains a `Set<Role>` relationship
- **Data seeding**: `AdminDataLoader` seeds the default roles (Admin, Super User, Catalog User, Order User) and assigns the Admin role to the default admin user
- **User management screens**: Need a role picker (multi-select or checkbox list) when creating/editing users
- **Audit log**: New table with scheduled cleanup; all significant user actions are logged automatically
