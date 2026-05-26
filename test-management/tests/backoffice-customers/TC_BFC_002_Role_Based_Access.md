# Role-Based Access for Customers Module

Verifies that the new "Customer Admin" role grants access to the Customers module while blocking access to the global dashboard and other modules, and that other roles are appropriately restricted.

## Metadata

- **Test ID:** TC_BFC_002
- **Version:** 1.1
- **Software Version:** >= 1.0.0
- **Domains:** Backoffice
- **Priority:** 🔴 Critical
- **Status:** ✅ Active
- **Execution Type:** Manual
- **Suite:** 🚀 Smoke
- **Requirements:**
  - BUS-EPIC-1: Backoffice
- **Tags:** `backoffice`, `security`, `rbac`, `customers`
- **Author:** AI Agent (2026-05-07)
- **Reviewers:**
  - AI Agent (2026-05-07)

## Preconditions

- The Posters Demo Store is running.
- Default roles are seeded in the database.
- A user with "System Admin" role exists.

## Test Data

| Field | Value |
| :--- | :--- |
| Customer Admin Role | `customers` module only |
| Catalog User Role | `dashboard`, `catalog` modules |

## Execution Targets

**Target Locales:**
- [x] EN-US
- [ ] EN-GB
- [ ] DE-DE
- [ ] SV-SE
- [ ] JA-JP

**Target Viewports:**
- [x] Desktop (Large)

---

## Steps

### 1. Login as System Admin

- **Action:** Log into the backoffice as a System Admin.
- **Verify:** The user is logged in and can access the User management section.

### 2. Create Customer Admin User

- **Action:** Navigate to Security -> Users, create a new user, and assign ONLY the "Customer Admin" role. Log out.
- **Verify:** The user is created successfully.

### 3. Login as Customer Admin

- **Action:** Log in using the newly created Customer Admin credentials.
- **Verify:** The user is authenticated. Since they lack access to the global dashboard, they should be redirected to `/backoffice/customers` (or see an access denied message if they try to access `/backoffice` directly).

### 4. Verify Customer Admin Sidebar

- **Action:** Inspect the sidebar navigation.
- **Verify:** ONLY the "Customers" module is visible. Global Dashboard, Security, Catalog, and Orders are NOT visible.

### 5. Verify Customer Admin Access Restriction

- **Action:** Attempt to manually navigate to `/backoffice/catalog` or `/backoffice/security/users`.
- **Verify:** The request is blocked by the ModuleAccessInterceptor (e.g., returns 403 Forbidden or redirects with an error).

### 6. Create Catalog User

- **Action:** Log back in as System Admin. Create a new user with ONLY the "Catalog User" role. Log out.
- **Verify:** The user is created successfully.

### 7. Login as Catalog User and Verify Restrictions

- **Action:** Log in as the Catalog User. Attempt to navigate to `/backoffice/customers`.
- **Verify:** The request is blocked by the ModuleAccessInterceptor. The Customers section is NOT visible in the sidebar navigation.

---

## Pass/Fail Criteria

- **Pass:** The Customer Admin role restricts access solely to the customers module. Users without the Customer Admin (or Business/System Admin) role cannot access the customers module.
- **Fail:** Users can bypass module restrictions by directly navigating to URLs or see unauthorized links in the sidebar.

---

## Postconditions

- Test users created during execution exist in the database.

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-05-07 | 1.0 | AI Agent | Initial creation |
| 2026-05-25 | 1.1 | Gemini (AI) | Added standard locales including JA-JP to Target Locales list |
