## MODIFIED Requirements

### Requirement: Backoffice Sidebar SHALL Display Navigation Items

The sidebar SHALL dynamically render navigation links based on the current user's role-based module access. Only modules the user has permission to access SHALL be displayed. Modules with submodules SHALL render as collapsible groups. The active module or submodule SHALL be visually highlighted.

#### Scenario: Sidebar renders permitted modules only

- **WHEN** an authenticated admin views the backoffice
- **THEN** the sidebar shows only the modules their assigned roles grant access to
- **AND** the Dashboard is always visible

#### Scenario: Admin module renders with collapsible submodules

- **WHEN** an admin with the Admin role views the sidebar
- **THEN** the Admin module is displayed as a collapsible group
- **AND** clicking it reveals: Users, Roles, Security Settings, Audit Log

#### Scenario: Non-admin user does not see Admin module

- **WHEN** a user without the Admin role views the sidebar
- **THEN** the Admin module and its submodules are not visible in the sidebar
