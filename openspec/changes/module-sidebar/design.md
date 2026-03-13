## Context

The backoffice uses Thymeleaf Layout Dialect (`layout:decorate`) for all pages. A `SidebarModelAdvice` class with `SidebarModule` / `SidebarSubmodule` record types exists but is currently a plain class — not a `@ControllerAdvice`. The `BackofficeModule` enum provides the module registry and URL-prefix routing, and roles map to modules via the `role_modules` join table.

**The Problem**: Thymeleaf Layout Dialect processes the layout template (`default.html`) in a separate pass from content pages. `@ModelAttribute` values added by a `@ControllerAdvice` are available to the content fragment, but NOT directly iterable via `th:each` in the layout template itself. This caused the sidebar loop to silently fail, so it was hardcoded as static HTML.

**Current module structure** (`BackofficeModule` enum) is also outdated — it uses a flat structure with top-level `PRODUCTS`, `CATEGORIES`, `ORDERS`, `SETTINGS`, `ADMIN` and only Admin submodules. The target IA has a completely different grouping.

## Goals / Non-Goals

**Goals:**
- Updated `BackofficeModule` enum reflecting the final module hierarchy (Dashboard, Security, Catalog, Customers, Orders — each with their own submodules)
- Dynamic sidebar rendered from the user's role-permitted modules, hierarchically
- All-or-nothing role access: a role's `moduleIds` contains only top-level module IDs; all submodules of that module are automatically granted
- Dashboard is **role-gated like every other module** — it is included in all four default roles so all users see it, but access is still interceptor-checked
- Role seeding updated to match new module IDs
- Active module highlighted server-side; parent group auto-expanded when submodule is active

**Non-Goals:**
- Fine-grained submodule-level access control (per spec: role grants full module including all sub)
- Implementing content pages for new submodules (Pricing, Variations, Import/Export, etc.) — stubs only
- Changing the Layout Dialect setup globally — solution scoped to sidebar only

## Decisions

### Thymeleaf Sidebar Rendering: Controller-Scoped Include vs. Layout Attribute

**Decision**: Pass the sidebar model as a `@ModelAttribute` from a `@ControllerAdvice` scoped to backoffice controllers, and render the sidebar using a **Thymeleaf fragment include** (`th:replace="~{backoffice/layout/sidebar :: sidebar}"`) inside `default.html`, rather than inlining `th:each` directly in the layout template.

**Rationale**: Layout Dialect processes `layout:decorate` templates after content fragments — the decorator sees the merged DOM, not the Spring model directly. However, if the layout template delegates sidebar rendering to a sub-fragment via `th:replace`, that fragment evaluation happens when the final merged template is processed, at which point the `@ModelAttribute` IS available. This sidesteps the Layout Dialect model timing issue without abandoning the dialect.

**Alternative considered**: Drop Thymeleaf Layout Dialect entirely, use plain `th:replace` fragments everywhere — works, but requires refactoring every single page template.

**Alternative considered**: Pass the sidebar model from every controller individually — works, but is repetitive and error-prone.

### Module Registry: Enum Fields vs. Access-Check Logic

**Decision**: Keep `BackofficeModule` as a Java enum but revise the entries. Access checking in `ModuleAccessInterceptor` and `SidebarModelAdvice` uses `getAccessModule()` (submodule inherits parent's module ID) — unchanged.

**Rationale**: The enum already has all the helper methods (`topLevelModules()`, `getSubmodules()`, `fromPath()`). Only the enum values themselves need updating.

### Role-Module Mapping: Top-Level IDs Only

**Decision**: The `role_modules` table stores **top-level module IDs only** (e.g. `security`, `catalog`). Submodules are never added to `role_modules` individually — access to a module automatically grants all its submodules.

**Rationale**: Consistent with the all-or-nothing access model. Simplifies the access check: just check if user's permitted module IDs contains the top-level module of the requested path.

### Stub Pages for New Submodules

**Decision**: New submodules listed in the hierarchy that have no implementation yet (Pricing, Variations, Import/Export, Settings under each top-level) get stub controller endpoints returning a "Coming Soon" template. This keeps the sidebar functional without blocking the sidebar work on full feature implementation.

**Rationale**: Sidebar must be able to navigate to all rendered menu items without a 404. Stubs are minimal: single `@GetMapping` + shared placeholder template.

## Risks / Trade-offs

- **th:replace fragment still subject to Thymeleaf model scope** → Mitigation: verify the fragment receives the `sidebarModules` attribute by testing with a real role-filtered model before wiring everything up.
- **Renaming URL prefixes breaks existing links** (e.g. `/backoffice/admin/**` → `/backoffice/security/**`) → Mitigation: redirect old paths to new, update all controller `@RequestMapping` annotations atomically.
- **Role seeding is conditional on empty table** — renaming modules while roles already exist in the H2 file-based DB won't auto-update `role_modules` entries → Mitigation: add a migration step in `AdminDataLoader` that deletes and re-seeds `role_modules` if they contain old module IDs (detectable by absence of `security` entry).

## Open Questions

- Should the `DASHBOARD` module require an explicit role entry, or be visible to all authenticated users? (Current assumption: always visible — no role check for dashboard.)
- Orders — is there a planned `Orders User` role or does it fall under `Super User`?
