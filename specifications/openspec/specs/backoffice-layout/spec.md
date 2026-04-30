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

### Requirement: Default Layout SHALL Use a Sidebar Fragment Include

The `default.html` layout template SHALL render the sidebar by including a dedicated `sidebar.html` fragment via `th:replace="~{backoffice/layout/sidebar :: sidebar}"`. The sidebar SHALL NOT be inlined directly in `default.html`. This allows the sidebar fragment to receive model attributes from Spring's model (including `@ControllerAdvice` contributions) without being blocked by Thymeleaf Layout Dialect's template processing order.

#### Scenario: Sidebar fragment receives sidebarModules model attribute

- **WHEN** any backoffice page is rendered
- **THEN** the `sidebar.html` fragment receives the `sidebarModules` model attribute
- **AND** iterates over it to render the navigation links

#### Scenario: Non-sidebar content still uses layout:decorate

- **WHEN** a content page uses `layout:decorate="~{backoffice/layout/default}"`
- **THEN** the layout wraps the page content as before
- **AND** the sidebar is rendered from the fragment, not hardcoded
