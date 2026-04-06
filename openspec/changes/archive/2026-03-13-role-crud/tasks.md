## 1. Repository & Service Layer

- [x] 1.1 Add `countByRoles(Role role)` method to `AdminUserRepository` to count users assigned to a given role
- [x] 1.2 Create `RoleService` with methods: `findAll()`, `findById()`, `save()`, `delete()`, `countUsersForRole()`, and `isDeleteAllowed()`

## 2. Controller — CRUD Endpoints

- [x] 2.1 Add `GET /backoffice/security/roles/new` endpoint returning the create form
- [x] 2.2 Add `POST /backoffice/security/roles` endpoint to handle create form submission with validation
- [x] 2.3 Add `GET /backoffice/security/roles/{id}/edit` endpoint returning the edit form (reject built-in roles)
- [x] 2.4 Add `POST /backoffice/security/roles/{id}` endpoint to handle edit form submission with validation
- [x] 2.5 Add `POST /backoffice/security/roles/{id}/delete` endpoint with built-in and user-assignment guards
- [x] 2.6 Update existing `list()` method to pass user counts and module display names to the view

## 3. Templates

- [x] 3.1 Rewrite `admin/roles/list.html` from card layout to table layout with Name, Description, Modules, Users, Actions columns
- [x] 3.2 Add "Create Role" button to the listing page header
- [x] 3.3 Add edit/delete action buttons for custom roles; show "Built-in" badge for built-in roles
- [x] 3.4 Add delete confirmation modal with user-count guard message
- [x] 3.5 Create `admin/roles/form.html` with name input, description input, and module checkbox list (reused for create and edit)

## 4. Verification

- [x] 4.1 Build the project (`mvn compile`) and confirm no errors
- [x] 4.2 Manual test: listing displays all roles with correct user counts
- [x] 4.3 Manual test: create a custom role, verify it appears in listing with builtIn=false
- [x] 4.4 Manual test: edit the custom role, verify changes persist
- [x] 4.5 Manual test: attempt to delete a role assigned to a user — verify it's blocked
- [x] 4.6 Manual test: delete an unassigned custom role — verify it's removed
- [x] 4.7 Manual test: verify built-in roles have no edit/delete buttons and direct URL access is rejected
