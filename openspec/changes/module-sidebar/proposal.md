## Why

The backoffice sidebar is currently hardcoded static HTML — every user sees every module regardless of their role. The `BackofficeModule` enum, `Role` entity, and `SidebarModelAdvice` infrastructure are in place, but the Thymeleaf Layout Dialect blocks `th:each` from receiving model attributes in the layout template. This change resolves that and delivers a fully dynamic, role-filtered sidebar with hierarchical module/submodule display.

## What Changes

- **Sidebar rendered dynamically** from the authenticated user's permitted modules — no more hardcoded HTML
- **Role-gated visibility**: a module (and all its submodules) is shown only if the user's roles include that module
- **Hierarchical display**: top-level modules render as collapsible groups; submodules indent beneath their parent
- **All-or-nothing access per role**: a role grants access to a full module including every submodule — no partial grants
- **Active state**: the current module/submodule is highlighted server-side by URL prefix matching
- **Bootstrap collapse**: Admin module group is collapsible; sub-items expand/collapse with animation

## Capabilities

### New Capabilities

_(none — sidebar rendering is a layout concern, not a new business capability)_

### Modified Capabilities

- `backoffice-navigation`: Requirement changes — sidebar MUST render dynamically from user's role-derived module list. Previously satisfied by static HTML workaround; now must comply with the role-filtering and hierarchy requirements.
- `backoffice-layout`: Layout template must change from static sidebar HTML to a dynamic Thymeleaf fragment that iterates the module model.

## Impact

- `default.html` — replace static `<ul>` nav with `th:each` loop over model (requires fragment-based include instead of Layout Dialect layout)
- `SidebarModelAdvice` — already exists, may need minor adjustment to ensure it populates via `@ModelAttribute` correctly
- `backoffice.css` — add collapse group styles, active highlight, submodule indentation
- No controller changes needed; no API changes; no storefront impact
