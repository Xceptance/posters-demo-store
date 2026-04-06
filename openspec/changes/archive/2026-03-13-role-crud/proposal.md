## Why

The backoffice currently only supports built-in, predefined roles, and the roles view is read-only. Administrators need to create, edit, and delete custom roles to tailor module access to their organization without requiring code changes.

## What Changes

- Add a listing screen for roles showing name, description, assigned modules, and a **user count** (how many users currently have that role), with edit and delete buttons for custom roles.
- Each user has exactly **one role** (single-role assignment).
- Allow creation of new custom roles with a name, description, and module access selection.
- Allow editing of custom roles (name, description, module assignments).
- Allow deletion of custom roles — but only if the role is **not currently assigned to any user** (user count = 0).
- Built-in roles (Admin, Super User, etc.) remain protected: no edit or delete buttons shown.
- Roles define which modules a user can access and see. Access is binary — a role either grants full module access or no access at all (no read/write distinction).

## Capabilities

### New Capabilities

### Modified Capabilities
- `backoffice-roles`: Remove the "No CRUD actions" constraint. Add create, edit, and delete operations for non-built-in roles. Add deletion guard for roles assigned to users.

## Impact

- **UI/Frontend**: Roles listing view updated with action buttons; new create/edit form; delete confirmation with in-use guard messaging.
- **Backend/Controllers**: New endpoints for role CRUD operations.
- **Database/Entities**: Leverage existing `builtIn` flag on `Role` entity; query user-role assignments before allowing deletion.
