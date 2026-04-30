## ADDED Requirements

### Requirement: Admin SHALL Be Able to Create Custom Roles

The system SHALL allow an administrator to create a new custom role by providing a unique name, a description, and selecting one or more top-level modules. The created role SHALL have `builtIn = false`.

#### Scenario: Successful role creation

- **WHEN** an admin navigates to the role create form
- **AND** enters a valid unique name, description, and selects at least one module
- **AND** submits the form
- **THEN** a new role is persisted with `builtIn = false`
- **AND** the admin is redirected to the roles listing

#### Scenario: Role creation with duplicate name

- **WHEN** an admin submits the create form with a name that already exists
- **THEN** the form is redisplayed with a validation error indicating the name is taken
- **AND** no role is created

#### Scenario: Role creation with missing required fields

- **WHEN** an admin submits the create form without a name or description
- **THEN** the form is redisplayed with validation errors for the missing fields

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
- **AND** confirms the deletion in the modal
- **THEN** the role and its module mappings are removed from the database
- **AND** the admin is redirected to the roles listing

#### Scenario: Deletion blocked for role assigned to users

- **WHEN** an admin attempts to delete a custom role that has one or more assigned users
- **THEN** the system displays a message indicating the role cannot be deleted because it is in use
- **AND** the role is not deleted

#### Scenario: Deletion blocked for built-in roles

- **WHEN** an admin attempts to delete a built-in role (via direct URL)
- **THEN** the system redirects to the roles listing without deleting

## MODIFIED Requirements

### Requirement: Roles Submodule SHALL List All Roles

The Admin > Roles submodule SHALL display all roles in a table. Each row shows the role name, description, the modules it grants access to (as badges), and the number of users currently assigned to that role. Custom roles show edit and delete action buttons; built-in roles show a "Built-in" badge and no action buttons.

#### Scenario: Roles list displays all roles with user counts

- **WHEN** an admin navigates to the Admin > Roles submodule
- **THEN** all roles are listed in a table with columns: Name, Description, Modules, Users, Actions
- **AND** each role shows the count of users currently assigned to it

#### Scenario: Custom roles show action buttons

- **WHEN** an admin views the roles list
- **THEN** custom roles (builtIn = false) display edit and delete buttons in the Actions column

#### Scenario: Built-in roles are protected

- **WHEN** an admin views the roles list
- **THEN** built-in roles display a "Built-in" badge and no edit or delete buttons

## REMOVED Requirements

### Requirement: No CRUD actions for roles
**Reason**: Replaced by the new create, edit, and delete operations for custom roles.
**Migration**: The roles listing now includes action buttons for custom roles.
