## MODIFIED Requirements

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

The sidebar SHALL be rendered server-side by iterating a model attribute (`sidebarModules`) computed from the user's role-permitted modules. The `sidebarModules` attribute SHALL be added to the model by a `@ControllerAdvice` applying to all backoffice controllers. The layout template SHALL render the sidebar via a Thymeleaf fragment include (`th:replace`), not by inlining `th:each` directly in the decorator.

#### Scenario: Sidebar shows only permitted top-level modules

- **WHEN** a user with the Catalog User role is authenticated
- **THEN** `sidebarModules` contains only Dashboard and Catalog
- **AND** the sidebar renders those two entries only

#### Scenario: Dashboard always visible

- **WHEN** any authenticated user loads a backoffice page
- **THEN** the Dashboard module is always included in `sidebarModules` regardless of roles
- **AND** the Dashboard link is always visible in the sidebar

#### Scenario: Active module is highlighted

- **WHEN** the user is on `/backoffice/security/users`
- **THEN** the Security group is expanded in the sidebar
- **AND** the Users submodule entry is visually highlighted as active
- **AND** the Security parent entry shows the expanded state

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
