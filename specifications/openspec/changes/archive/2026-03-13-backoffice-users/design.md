## Context

The backoffice currently has a flat `AdminUser` entity (id, username, password, displayName, createdAt) and a hardcoded sidebar with static nav items. `SecurityConfig` uses a single filter chain that only distinguishes authenticated vs. unauthenticated. All admin users see everything.

We need to introduce role-based module access, a hierarchical sidebar, an Admin module with user/role/audit management, and server-side route protection.

## Goals / Non-Goals

**Goals:**
- Role entity with module mapping, many-to-many with AdminUser
- Code-defined module registry (enum or config class) with parent/child support
- Dynamic sidebar rendering based on user roles
- Spring Security route-level enforcement per module
- Admin module with Users (CRUD + search/filter/pagination), Roles (list-only), Security Settings (placeholder), Audit Log (searchable, 180-day retention)
- Default seeded roles (immutable)
- Safety guardrails: no self-delete, last-admin protection, Admin module restricted to Admin role

**Non-Goals:**
- Custom role creation/editing (roles are seeded and immutable for now)
- Fine-grained read/write permissions within a module
- Password reset via email/link (temporary direct-set approach)
- Security Settings implementation beyond placeholder
- Storefront changes

## Decisions

### Module Registry: Java Enum vs. Database Table

**Decision**: Use a Java enum `BackofficeModule` with fields (id, displayName, icon, urlPrefix, parentModule). 

**Rationale**: Modules are tightly coupled to the codebase (each module needs controllers, templates, routes). Adding a module always requires code changes anyway, so a DB table adds indirection without benefit. An enum is compile-time safe, easily iterable, and simple to extend.

**Alternative considered**: Database `modules` table — more flexible but premature; we don't need dynamic module creation.

### Role-Module Mapping: Join Table vs. Embedded Collection

**Decision**: Use a `role_modules` join table that maps `role_id` to a module string identifier (the enum name).

**Rationale**: Clean relational model, easy to query "which modules does this role have?" and "which roles can access this module?". String-based module IDs keep the join table decoupled from enum ordinals.

### Route Protection: @PreAuthorize vs. Filter Chain Rules

**Decision**: Use a custom `ModuleAccessInterceptor` (Spring `HandlerInterceptor`) that checks the current URL against the user's permitted modules.

**Rationale**: Module-to-URL mapping is already defined in the enum. An interceptor can extract the module from the URL prefix and check against the user's role set in one place, rather than scattering `@PreAuthorize` annotations across every controller. On access denial, it renders a "no permissions" error page.

**Alternative considered**: `@PreAuthorize("hasModule('PRODUCTS')")` on each controller — works but is repetitive and error-prone for new modules.

### Sidebar Rendering: Server-Side via Thymeleaf

**Decision**: Build the sidebar model server-side in a `@ControllerAdvice`/`@ModelAttribute` that computes the user's visible modules and passes them to the layout template. The sidebar template iterates over this model.

**Rationale**: No JavaScript needed for initial render. Collapsible submodules use Bootstrap 5 collapse component with CSS transitions. Active state is computed server-side by matching the current URL to module URL prefixes.

### Audit Log: AOP vs. Explicit Service Calls

**Decision**: Use explicit `AuditLogService.log(...)` calls in service methods rather than AOP.

**Rationale**: We need specific detail messages per action (e.g., "Set role 'Catalog User' on user 'john'"). AOP would require complex pointcut definitions and reflection to extract meaningful details. Explicit calls are clearer, testable, and let us control exactly what gets logged.

### Audit Log Cleanup: @Scheduled Task

**Decision**: Use a `@Scheduled` method that runs daily to delete entries older than 180 days.

**Rationale**: Simple, built-in Spring mechanism. No external scheduler needed. The query `DELETE FROM audit_log WHERE timestamp < :cutoff` is efficient with an index on timestamp.

## Risks / Trade-offs

- **Enum-based modules require code changes to add modules** → Acceptable trade-off for this stage; if dynamic modules are needed later, migration to a DB-backed registry is straightforward.
- **No custom roles** → Users can't tailor access beyond the seeded roles. Mitigated by seeding roles that cover common use cases; custom roles can be added later by making the Role entity editable.
- **Explicit audit logging could be forgotten in new code** → Mitigated by code review discipline and potentially a checklist in the contributor guide.
- **180-day cleanup deletes data permanently** → No archive mechanism. Acceptable for a demo store; production would likely need log export first.
