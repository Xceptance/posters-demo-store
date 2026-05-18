## Requirements

### Requirement: Role Entity SHALL Define Module Access

The system SHALL maintain a `Role` entity with fields: `id` (Long, auto-increment), `name` (unique, not null), `description` (optional), and a set of permitted modules. Each role maps to one or more backoffice modules. There is no read/write distinction — a role either grants full access to a module or no access at all. Roles can be built-in (immutable) or custom (editable).

#### Scenario: Role record structure

- **WHEN** inspecting the `roles` table
- **THEN** it contains columns for `id`, `name`, `description`, `built_in`
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

### Requirement: Default Roles SHALL Be Seeded with Correct Module IDs

The default roles SHALL be seeded on first startup using top-level module IDs. The `AdminDataLoader` SHALL detect and re-seed `role_modules` entries if they contain outdated module IDs (i.e. if the `security` module ID is absent from all role entries). A new built-in "Customer Admin" role is included.

#### Default Role Definitions

| Role | Module IDs |
|---|---|
| System Admin | `dashboard`, `security`, `catalog`, `customers`, `orders` |
| Business Admin | `dashboard`, `catalog`, `customers`, `orders` |
| Customer Admin | `customers` |
| Catalog User | `dashboard`, `catalog` |
| Order User | `dashboard`, `orders` |

#### Scenario: Roles seeded with module IDs on fresh start

- **WHEN** the application starts for the first time
- **AND** the `roles` table is empty
- **THEN** roles are seeded with the module IDs from the table above
- **AND** `security` is only included in the System Admin role

#### Scenario: Role_modules re-seeded on upgrade from old module IDs

- **WHEN** the application starts
- **AND** the `roles` table has entries
- **AND** no role contains the `security` module ID
- **THEN** all `role_modules` entries are cleared and re-seeded with updated IDs
- **AND** the roles themselves (name, description, builtIn) are preserved

### Requirement: Roles Submodule SHALL List All Roles

The Admin > Roles submodule SHALL display all roles in a table. Each row shows the role name, description, the modules it grants access to (as badges), and the number of users currently assigned to that role. Custom roles show edit and delete action buttons; built-in roles show a "Built-in" badge and no action buttons.

#### Scenario: Roles list displays all roles with user counts

- **WHEN** an admin navigates to the Admin > Roles submodule
- **THEN** all roles are listed in a table with columns: Name, Description, Modules, Users, Actions
- **AND** each role shows the count of users currently assigned to it

#### Scenario: Custom roles show action buttons

- **WHEN** an admin views the roles list
- **THEN** custom roles (builtIn = false) display edit and delete buttons in the Actions column
- **AND** clicking the role name navigates to the edit form

#### Scenario: Built-in roles are protected

- **WHEN** an admin views the roles list
- **THEN** built-in roles display a "Built-in" badge and no edit or delete buttons

### Requirement: Admin SHALL Be Able to Create Custom Roles

The system SHALL allow an administrator to create a new custom role by providing a unique name, an optional description, and selecting one or more top-level modules. The created role SHALL have `builtIn = false`.

#### Scenario: Successful role creation

- **WHEN** an admin navigates to the role create form
- **AND** enters a valid unique name and selects at least one module
- **AND** submits the form
- **THEN** a new role is persisted with `builtIn = false`
- **AND** the admin is redirected to the roles listing

#### Scenario: Role creation with duplicate name

- **WHEN** an admin submits the create form with a name that already exists
- **THEN** the form is redisplayed with a validation error indicating the name is taken
- **AND** previously entered data is preserved
- **AND** no role is created

#### Scenario: Role creation with missing name

- **WHEN** an admin submits the create form without a name
- **THEN** the form is redisplayed with a validation error
- **AND** previously entered data is preserved

### Requirement: Admin SHALL Be Able to Edit Custom Roles

The system SHALL allow an administrator to edit a custom (non-built-in) role's name, description, and module assignments.

#### Scenario: Successful role edit

- **WHEN** an admin opens the edit form for a custom role
- **AND** changes the name, description, or module selections
- **AND** submits the form
- **THEN** the role is updated in the database
- **AND** the admin is redirected to the roles listing

#### Scenario: Edit form not available for built-in roles

- **WHEN** an admin attempts to access the edit form for a built-in role (via direct URL)
- **THEN** the system redirects to the roles listing without making changes

### Requirement: Admin SHALL Be Able to Delete Custom Roles

The system SHALL allow deletion of custom roles that are not currently assigned to any user. Built-in roles SHALL NOT be deletable.

#### Scenario: Successful deletion of unassigned custom role

- **WHEN** an admin clicks delete on a custom role with zero assigned users
- **AND** confirms the deletion in a Bootstrap modal
- **THEN** the role and its module mappings are removed from the database
- **AND** the admin is redirected to the roles listing

#### Scenario: Deletion blocked for role assigned to users

- **WHEN** an admin attempts to delete a custom role that has one or more assigned users
- **THEN** the delete modal displays a message indicating the role cannot be deleted because it is in use
- **AND** the role is not deleted

#### Scenario: Deletion blocked for built-in roles

- **WHEN** an admin attempts to delete a built-in role (via direct URL)
- **THEN** the system redirects to the roles listing without deleting
