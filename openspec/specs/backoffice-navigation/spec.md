## ADDED Requirements

### Requirement: Module Registry SHALL Use the Revised Hierarchy

The `BackofficeModule` enum SHALL define the following top-level modules and their submodules. The old flat structure (PRODUCTS, CATEGORIES, ORDERS, SETTINGS, ADMIN as top-levels) is replaced.

| Top-Level Module | ID | Submodule IDs |
|---|---|---|
| Dashboard | `dashboard` | _(none — direct link)_ |
| Security | `security` | `security-users`, `security-audit-log`, `security-roles`, `security-import-export`, `security-settings` |
| Catalog | `catalog` | `catalog-dashboard`, `catalog-categories`, `catalog-products`, `catalog-variations`, `catalog-pricing`, `catalog-import-export`, `catalog-settings` |
| Customers | `customers` | `customers-dashboard`, `customers-list`, `customers-import-export`, `customers-settings` |
| Orders | `orders` | `orders-dashboard`, `orders-list`, `orders-export` |

#### Scenario: Module registry returns all top-level modules

- **WHEN** querying `BackofficeModule.topLevelModules()`
- **THEN** it returns: Dashboard, Security, Catalog, Customers, Orders — in that order
- **AND** each has the correct URL prefix mapping

#### Scenario: Submodules reference their parent

- **WHEN** querying `BackofficeModule.getSubmodules()` on Security
- **THEN** it returns: Users, Audit Log, Roles, Import/Export, Settings — in order
- **AND** each submodule's `getAccessModule()` returns the Security module

### Requirement: Sidebar SHALL Render Dynamically from User Role Permissions

The sidebar SHALL be rendered server-side by iterating a model attribute (`sidebarModules`) computed from the user's role-permitted modules. The `sidebarModules` attribute SHALL be added to the model by a `@ControllerAdvice` applying to all backoffice controllers. The layout template SHALL render the sidebar via a Thymeleaf fragment include (`th:replace`), not by inlining `th:each` directly in the decorator. The combined set of permitted modules across all of a user's roles determines visibility.

#### Scenario: Sidebar shows only permitted top-level modules

- **WHEN** a user with the Catalog User role is authenticated
- **THEN** `sidebarModules` contains only Dashboard and Catalog
- **AND** the sidebar renders those two entries only

#### Scenario: Dashboard visible because it is in all roles

- **WHEN** any authenticated user loads a backoffice page
- **THEN** the Dashboard module is visible in the sidebar because `dashboard` is included in every default role
- **AND** access to `/backoffice/` is still checked by `ModuleAccessInterceptor` like any other module

#### Scenario: Active module is highlighted

- **WHEN** the user is on `/backoffice/security/users`
- **THEN** the Security group is expanded in the sidebar
- **AND** the Users submodule entry is visually highlighted as active
- **AND** the Security parent entry shows the expanded state

### Requirement: Sidebar SHALL Display Hierarchical Module/Submodule Navigation

Modules that have submodules SHALL render as collapsible groups in the sidebar. Clicking a parent module toggles the visibility of its submodules. The active submodule SHALL be visually highlighted, and its parent group SHALL be expanded.

#### Scenario: Admin module shows collapsible submodules

- **WHEN** an admin views the sidebar
- **THEN** the Admin module is displayed as a collapsible group
- **AND** clicking it reveals submodules: Users, Roles, Security Settings, Audit Log

#### Scenario: Modules without submodules render as direct links

- **WHEN** a module has no submodules (e.g., Dashboard, Products)
- **THEN** it renders as a simple clickable link in the sidebar
- **AND** clicking it navigates directly to the module page

### Requirement: Role-Module Access SHALL Be All-or-Nothing at the Top-Level

The `role_modules` table SHALL store **top-level module IDs only**. Access to a top-level module automatically grants access to all its submodules. There SHALL be no submodule-level entries in `role_modules`.

#### Scenario: Accessing a submodule checks the parent module

- **WHEN** `ModuleAccessInterceptor` checks access to `/backoffice/catalog/products`
- **THEN** it resolves the path to the `catalog` top-level module
- **AND** checks if the user's permitted module IDs contain `catalog`
- **AND** grants access if so — without a specific `catalog-products` entry

#### Scenario: Role grants full module with all submodules

- **WHEN** a role has `catalog` in its `role_modules` entries
- **THEN** the user can access all catalog submodule URLs
- **AND** all catalog submodule links are visible in the sidebar

### Requirement: Stub Endpoints SHALL Exist for All Registered Submodules

Every submodule defined in the `BackofficeModule` enum SHALL have a reachable endpoint. Submodules without a full implementation SHALL return a shared "Coming Soon" placeholder template. This ensures the sidebar is fully navigable without 404 errors.

#### Scenario: Navigating to an unimplemented submodule

- **WHEN** a user clicks a stub submodule link (e.g. Catalog > Pricing)
- **THEN** the page loads without error
- **AND** displays a "Coming Soon" placeholder with the module name

### Requirement: Unauthorized Module Access SHALL Return a Permission Error

When a user directly navigates to a module URL they do not have access to, the system SHALL return a "no permissions" error page instead of the module content. The sidebar SHALL NOT display the module.

#### Scenario: Direct URL access to unauthorized module

- **WHEN** a Catalog User navigates directly to `/backoffice/admin/users`
- **THEN** the system renders a permission error page
- **AND** the error message indicates insufficient permissions

#### Scenario: Direct URL access to authorized module

- **WHEN** a Catalog User navigates directly to `/backoffice/products`
- **THEN** the module page renders normally
