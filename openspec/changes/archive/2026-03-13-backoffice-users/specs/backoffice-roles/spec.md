## ADDED Requirements

### Requirement: Role Entity SHALL Define Module Access

The system SHALL maintain a `Role` entity with fields: `id` (Long, auto-increment), `name` (unique, not null), `description` (not null), and a set of permitted modules. Each role maps to one or more backoffice modules. There is no read/write distinction — a role either grants full access to a module or no access at all.

#### Scenario: Role record structure

- **WHEN** inspecting the `roles` table
- **THEN** it contains columns for `id`, `name`, `description`
- **AND** `name` has a unique constraint

#### Scenario: Role-to-module mapping

- **WHEN** inspecting the `role_modules` join table
- **THEN** each row maps a `role_id` to a `module_id` (string identifier)
- **AND** a role can have multiple module mappings

### Requirement: Module Registry SHALL Define All Navigable Modules

The system SHALL maintain a registry of all backoffice modules. Each module has an `id` (string, e.g., `products`, `orders`), a `displayName`, a Material Symbols `icon`, a `urlPrefix` (e.g., `/backoffice/products`), an `order` for sidebar sorting, and an optional `parentModule` reference for submodules. The registry is code-defined (not database-driven).

#### Scenario: Module registry contains all sidebar entries

- **WHEN** querying the module registry
- **THEN** it returns entries for all top-level modules: Dashboard, Products, Categories, Customers, Orders, Settings, Admin
- **AND** the Admin module has submodules: Users, Roles, Security Settings, Audit Log

#### Scenario: Each module has a URL prefix

- **WHEN** querying a module by its id
- **THEN** it returns a `urlPrefix` that matches the corresponding backoffice URL pattern

### Requirement: Default Roles SHALL Be Seeded at Startup

The system SHALL seed default roles on startup if no roles exist. Default roles are immutable — they cannot be edited or deleted via the UI, only assigned to users. The Backoffice Admin module permission is reserved for the built-in Admin role and cannot be set or unset via the UI.

#### Scenario: Default roles seeded on first startup

- **WHEN** the application starts for the first time
- **AND** the `roles` table is empty
- **THEN** the following roles are created:
  - **Admin** — access to all modules including the Admin module (Backoffice Admin)
  - **Super User** — access to all modules except the Admin module
  - **Catalog User** — access to product/catalog-related modules
  - **Order User** — access to order-related modules

#### Scenario: Default roles are immutable

- **WHEN** an admin views the roles list in the Admin > Roles submodule
- **THEN** each role displays its name, description, and permitted modules
- **AND** no edit or delete actions are available for default roles

#### Scenario: Subsequent startup does not duplicate roles

- **WHEN** the application starts again
- **AND** the `roles` table already contains roles
- **THEN** no additional roles are created

### Requirement: Roles Submodule SHALL List All Roles

The Admin > Roles submodule SHALL display all roles in a simple list. Each entry shows the role name, description, and the modules it grants access to. Since roles are immutable and the total number is small, no search, filtering, or pagination is required.

#### Scenario: Roles list displays all roles

- **WHEN** an admin navigates to the Admin > Roles submodule
- **THEN** all roles are listed with their name, description, and permitted modules

#### Scenario: No CRUD actions for roles

- **WHEN** an admin views the roles list
- **THEN** there are no create, edit, or delete buttons
- **AND** roles can only be assigned to users via the Users submodule
