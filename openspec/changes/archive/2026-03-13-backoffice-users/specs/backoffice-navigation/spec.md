## ADDED Requirements

### Requirement: Sidebar SHALL Dynamically Render Based on User Roles

The sidebar SHALL display only the modules (and their submodules) that the current user has access to, based on their assigned roles. The combined set of permitted modules across all of a user's roles determines visibility. The Dashboard module SHALL always be visible to all authenticated users.

#### Scenario: User sees only permitted modules

- **WHEN** a user with the Catalog User role logs in
- **THEN** the sidebar shows Dashboard and product/catalog-related modules only
- **AND** modules like Orders, Admin, and Settings are not visible

#### Scenario: Admin sees all modules

- **WHEN** a user with the Admin role logs in
- **THEN** the sidebar shows all modules including the Admin module with its submodules

#### Scenario: User with multiple roles sees union of modules

- **WHEN** a user has both Catalog User and Order User roles
- **THEN** the sidebar shows Dashboard, product/catalog modules, and order modules
- **AND** Admin and Settings modules are not visible

### Requirement: Sidebar SHALL Display Hierarchical Module/Submodule Navigation

Modules that have submodules SHALL render as collapsible groups in the sidebar. Clicking a parent module toggles the visibility of its submodules. The active submodule SHALL be visually highlighted, and its parent group SHALL be expanded.

#### Scenario: Admin module shows collapsible submodules

- **WHEN** an admin views the sidebar
- **THEN** the Admin module is displayed as a collapsible group
- **AND** clicking it reveals submodules: Users, Roles, Security Settings, Audit Log

#### Scenario: Active submodule expands its parent group

- **WHEN** a user navigates to Admin > Users
- **THEN** the Admin group is expanded in the sidebar
- **AND** the Users submodule is visually highlighted as active

#### Scenario: Modules without submodules render as direct links

- **WHEN** a module has no submodules (e.g., Dashboard, Products)
- **THEN** it renders as a simple clickable link in the sidebar
- **AND** clicking it navigates directly to the module page

### Requirement: Unauthorized Module Access SHALL Return a Permission Error

When a user directly navigates to a module URL they do not have access to, the system SHALL return a "no permissions" error page instead of the module content. The sidebar SHALL NOT display the module.

#### Scenario: Direct URL access to unauthorized module

- **WHEN** a Catalog User navigates directly to `/backoffice/admin/users`
- **THEN** the system renders a permission error page
- **AND** the error message indicates insufficient permissions

#### Scenario: Direct URL access to authorized module

- **WHEN** a Catalog User navigates directly to `/backoffice/products`
- **THEN** the module page renders normally
