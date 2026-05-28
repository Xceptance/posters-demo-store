# Backoffice Login and Logout

Verifies that an administrative user can log into the backoffice successfully, navigate the dashboard, and log out securely.

## Metadata

- **Test ID:** TC_BFC_001
- **Version:** 1.5
- **Software Version:** >= 1.0.0
- **Domains:** Backoffice
- **Priority:** 🔴 Critical
- **Status:** ✅ Active
- **Execution Type:** Manual
- **Suite:** 🚀 Smoke
- **Requirements:**
  - BUS-EPIC-1: Backoffice
- **Tags:** `backoffice`, `security`, `login`
- **Author:** AI Agent (2026-05-07)
- **Reviewers:**
  - AI Agent (2026-05-07)

## Preconditions

- The Posters Demo Store is running.
- Default roles and admin user are seeded in the database.

## Test Data

| Field | Value |
| :--- | :--- |
| Username | `admin` |
| Password | `admin-2026!` |

## Execution Targets

**Target Viewports:**
- [x] Desktop (Large)

---

## Steps

### 1. Access Backoffice Login

- **Action:** Navigate to `/backoffice/login` in the browser.
- **Verify:** The backoffice login page is displayed with username and password fields.

### 2. Enter Valid Credentials

- **Action:** Enter the `admin` username and `admin-2026!` password, then click "Sign In".
- **Verify:** The user is authenticated and redirected to the global backoffice dashboard (`/backoffice`).

### 3. Verify Session

- **Action:** Reload the page.
- **Verify:** The user remains logged in and the dashboard is still visible.

### 4. Logout

- **Action:** Click on the user profile dropdown in the header and select "Log out".
- **Verify:** The user is logged out, redirected back to `/backoffice/login?logout`, and a success message is displayed.

### 5. Verify Logout Security

- **Action:** Attempt to navigate directly to `/backoffice` without logging in.
- **Verify:** The user is blocked and redirected back to the login page.

---

## Pass/Fail Criteria

- **Pass:** Admin user can successfully log in, view the dashboard, and log out cleanly without being able to access the dashboard post-logout.
- **Fail:** User cannot log in, session does not persist, or user can access backoffice routes after logging out.

---

## Postconditions

- User session is terminated.

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-05-07 | 1.0 | AI Agent | Initial creation |
| 2026-05-25 | 1.1 | Gemini (AI) | Added JA-JP locale to Target Locales list |
| 2026-05-26 | 1.2 | Antigravity (AI) | Relocated to backoffice-users directory |
| 2026-05-26 | 1.3 | Antigravity (AI) | Relocated to nested backoffice/users directory |
| 2026-05-26 | 1.4 | Antigravity (AI) | Updated Step 4 verification redirect to /backoffice/login?logout |
| 2026-05-26 | 1.5 | Antigravity (AI) | Removed Target Locales from Execution Targets |
