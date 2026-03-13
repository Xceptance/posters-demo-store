## ADDED Requirements

### Requirement: AdminUser SHALL Have Role Assignments

The `AdminUser` entity SHALL support a many-to-many relationship with roles. Each admin user SHALL have one or more roles. The combined set of permitted modules across all assigned roles determines the user's access.

#### Scenario: Admin user has roles

- **WHEN** inspecting an admin user record
- **THEN** it has one or more roles assigned via the `admin_user_roles` join table

#### Scenario: User access is the union of all role permissions

- **WHEN** a user has both Catalog User and Order User roles
- **THEN** the user has access to all modules granted by either role

### Requirement: User Management SHALL Support Search, Filtering, and Pagination

The Admin > Users submodule SHALL display all admin users in a paginated table with search and filtering capabilities. The table SHALL show username, display name, assigned roles, and created date.

#### Scenario: Users listed in paginated table

- **WHEN** an admin navigates to Admin > Users
- **THEN** a paginated table displays all admin users with their username, display name, roles, and creation date

#### Scenario: Search by username or display name

- **WHEN** an admin enters a search term
- **THEN** the table filters to show users matching the search in username or display name

#### Scenario: Filter by role

- **WHEN** an admin selects a role filter
- **THEN** the table shows only users who have the selected role

### Requirement: Admin SHALL Be Able to Reset User Passwords

An admin SHALL be able to set a new password for any user directly. This is a temporary approach — a more sophisticated mechanism (e.g., password reset links) will be implemented later. The new password SHALL be BCrypt-hashed before storage.

#### Scenario: Admin resets a user's password

- **WHEN** an admin sets a new password for a user
- **THEN** the user's password is updated with a BCrypt hash of the new value
- **AND** the user can log in with the new password

### Requirement: Admin SHALL Assign Roles When Creating or Editing Users

The user creation and edit forms SHALL include a role picker (checkbox list) for assigning roles. At least one role SHALL be required. The Admin role assignment SHALL be restricted — only users who themselves have the Admin role can assign or remove the Admin role.

#### Scenario: Creating a user with roles

- **WHEN** an admin creates a new user
- **THEN** the creation form includes a role picker
- **AND** the admin must select at least one role

#### Scenario: Editing a user's roles

- **WHEN** an admin edits a user
- **THEN** the edit form shows the current role assignments
- **AND** the admin can add or remove roles

### Requirement: User Self-Deletion SHALL Be Prevented

An admin user SHALL NOT be able to delete their own account. The delete action SHALL be hidden or disabled for the currently logged-in user's own record.

#### Scenario: Delete action hidden for own account

- **WHEN** an admin views the user list
- **THEN** the delete action is not available for their own user record

### Requirement: Last Admin-Role User SHALL Be Protected

The system SHALL prevent deletion or demotion of the last user with the Admin role. If only one user has the Admin role, that user cannot be deleted and the Admin role cannot be removed from them.

#### Scenario: Cannot delete last admin

- **WHEN** an admin attempts to delete the only remaining Admin-role user
- **THEN** the operation is rejected with an error message
- **AND** the user remains in the system

#### Scenario: Cannot remove Admin role from last admin

- **WHEN** an admin attempts to remove the Admin role from the only remaining Admin-role user
- **THEN** the operation is rejected with an error message
- **AND** the Admin role remains assigned

## MODIFIED Requirements

### Requirement: Default Admin Account SHALL Be Seeded at Startup

The system SHALL seed a default admin account on startup if no admin users exist. The default account SHALL use username `admin` and password `admin-2026!` (BCrypt-hashed). The seeding MUST be idempotent. The default admin user SHALL be assigned the Admin role.

#### Scenario: First startup seeds admin account with Admin role

- **WHEN** the application starts for the first time
- **AND** the `admin_users` table is empty
- **THEN** a default admin account is created with username `admin` and display name `Administrator`
- **AND** the password is BCrypt-hashed
- **AND** the Admin role is assigned to this user

#### Scenario: Subsequent startup does not duplicate admin

- **WHEN** the application starts again
- **AND** the `admin_users` table already contains one or more users
- **THEN** no additional admin accounts are created
