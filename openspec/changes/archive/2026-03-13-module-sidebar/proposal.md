## Why

The backoffice sidebar is currently hardcoded static HTML showing a flat, incomplete module list. The module structure itself needs to be revised to match the intended information architecture, and then rendered dynamically so only modules permitted by the user's roles are visible.

## What Changes

- **Revised module hierarchy** — the top-level module structure is redefined to match the intended IA (see below)
- **Sidebar rendered dynamically** from the authenticated user's permitted modules — no more hardcoded HTML
- **Role-gated visibility** — a module (and **all** its submodules) is shown only if the user's roles include that module. Access is all-or-nothing: a role grants the full module including every submodule, no partial grants
- **Hierarchical display** — top-level modules are collapsible groups; submodules indent beneath their parent, highlighted when active
- **Active state** — current module/submodule highlighted server-side by URL prefix matching
- **Bootstrap collapse** — collapsible parent groups with smooth animation

### Module Hierarchy

| Top-Level | Submodules |
|---|---|
| **Dashboard** | _(none — direct link)_ |
| **Security** | Users · Audit Log · Roles · Import/Export · Settings |
| **Catalog** | Dashboard · Categories · Products · Variations & Attributes · Pricing · Import/Export · Settings |
| **Customers** | Dashboard · Customers · Import/Export · Settings |
| **Orders** | Dashboard · Orders (search + state editing) · Export |

## Capabilities

### New Capabilities

_(none — sidebar rendering is a layout concern, not a new business capability)_

### Modified Capabilities

- `backoffice-navigation`: Requirement changes — sidebar MUST render dynamically from user's role-derived module list, with the new top-level hierarchy above. Previously satisfied by static HTML workaround.
- `backoffice-layout`: Layout template must change from static sidebar HTML to a dynamic Thymeleaf fragment that iterates the module model.
- `backoffice-roles`: The `BackofficeModule` enum and role-module seeding must be updated to reflect the new module names and hierarchy (Security replaces Admin; Catalog and Customers gain submodules).

## Impact

- `BackofficeModule.java` — redefine enum values to match new hierarchy (Security top-level with submodules; Catalog and Customers submodules)
- `AdminDataLoader.java` — update role-module seeding to use new module IDs
- `ModuleAccessInterceptor.java` — URL prefix matching updates for new paths (e.g. `/backoffice/security/**`)
- `default.html` — replace static `<ul>` nav with `th:each` loop (requires fragment-based include to bypass Thymeleaf Layout Dialect issue)
- `SidebarModelAdvice.java` — confirm `@ModelAttribute` populates correctly in fragment context
- `backoffice.css` — collapsible group styles, active highlight, submodule indentation
- URL routing — controller `@RequestMapping` paths may need updating to match new structure
