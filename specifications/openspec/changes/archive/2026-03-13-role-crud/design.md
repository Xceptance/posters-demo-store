## Context

The backoffice currently has a `Role` entity with a `builtIn` flag and a read-only listing view. Roles control which top-level modules a user can see/access (binary: full access or none). The `AdminUser` entity uses a `@ManyToMany` relationship (`Set<Role>`) via the `admin_user_roles` join table, but the business rule is **one role per user**.

The `RoleController` currently only has a `GET /backoffice/security/roles` listing endpoint. The view renders a card-based layout showing role name, description, built-in badge, and permitted module badges.

## Goals / Non-Goals

**Goals:**
- Full CRUD for custom (non-built-in) roles: create, edit, delete
- Listing screen with name, description, module badges, user count, and action buttons
- Deletion guard: prevent deleting roles assigned to at least one user
- Built-in roles are read-only (no edit/delete buttons shown)
- Module selection via checkbox list of top-level `BackofficeModule` entries

**Non-Goals:**
- Changing the `AdminUser` ↔ `Role` relationship from `ManyToMany` to `ManyToOne` in this change (enforce single-role at application level for now)
- Granular per-module permissions (read/write distinction)
- Role ordering or priorities

## Decisions

### 1. Table-based listing instead of cards
The current card layout does not accommodate action buttons or user counts well. Switch to a Bootstrap table with columns: Name, Description, Modules, Users, Actions. This is consistent with the user management listing approach.

**Alternatives considered:** Keep cards with action overlays — rejected because tables are more scannable for CRUD-heavy views.

### 2. Inline delete with confirmation modal
Use a Bootstrap modal for delete confirmation. The modal shows the role name and explains why deletion is blocked if users are assigned.

**Alternatives considered:** Inline confirmation with button state toggle — rejected for consistency with typical admin patterns.

### 3. Separate form page for create/edit
Use a dedicated form page (`form.html`) for both create and edit, following the same pattern as the existing user form (`admin/users/form.html`). The form includes text inputs for name/description and checkboxes for top-level modules.

**Alternatives considered:** Inline editing in the table — rejected for complexity and inconsistency with the existing user form pattern.

### 4. User count via repository query
Add a `countByRolesContaining(Role role)` method to `AdminUserRepository` (or equivalent) to get the number of users assigned to each role. This is used in the listing and in the deletion guard.

### 5. Keep ManyToMany relationship, enforce single-role in the UI
The current `Set<Role>` on `AdminUser` stays. The user form already assigns a single role. Changing the JPA mapping is a separate concern and avoids a schema migration in this change.

## Risks / Trade-offs

- **Name uniqueness violation** → Return to form with validation error message (Spring `@Valid` + `DataIntegrityViolationException` catch)
- **Concurrent deletion of in-use role** → The `countByRolesContaining` check before delete is sufficient; no strict DB-level constraint needed for a demo store
- **Built-in flag bypass** → Controller-level guard ensures built-in roles are never modified/deleted, even by direct URL access
