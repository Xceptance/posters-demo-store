## MODIFIED Requirements

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
