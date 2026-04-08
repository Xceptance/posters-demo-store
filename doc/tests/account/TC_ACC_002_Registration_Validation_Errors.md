# Registration Validation Limits

This test verifies that the system strictly rejects invalid registration attempts, specifically targeting duplicate email address constraints and minimum password length constraints set in the UI HTML markup.

## Metadata

- **Test ID:** TC_ACC_002
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🔴 Critical
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** 🔄 Regression, 🔒 Security
- **Requirements:**
  - [REQ-ID or link to backlog item]
- **Tags:** `account`, `registration`, `validation`, `negative`
- **Author:** Antigravity (AI) (2026-04-08)
- **Reviewers:**
  - [Human]

## Comments

> [!CAUTION]
> As of current standard, backend logic relies strictly on frontend `minlength="6"` for complexity and standard email unique constraints. Watch closely for raw HTML escaping in flash messages!

## Preconditions

- The Posters Demo Store is running.
- The user `johndoe.test@example.com` must already exist in the database (e.g. via `TC_ACC_001`).

## Test Data

| Data ID | First Name | Last Name | Email | Password | Exception Target |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `Duplicate Email` | Jane | Smith | `johndoe.test@example.com` | `testpass123` | Backend DB Reject |
| `Short Password` | Bob | Tester | `unique.bob@example.com` | `12345` | Frontend Reject |

## Execution Targets

**Target Locales:**
- [x] EN-US
- [x] DE-DE

**Target Viewports:**
- [x] Desktop (1920x1080)

## Steps

### 1. Test Duplicate Email Constraints
- **Action:** Navigate to `/{locale}/register` and fill out the form using the `Duplicate Email` data.
- **Action:** Click "Register".
- **Verify:** The application aborts creation and reloads the registration page.
- **Verify:** A flash error clearly states "Email already in use." (or localized equivalent) directly on the screen without escaping artifacts (like `&quot;`).

### 2. Test Minimum Password Length constraints
- **Action:** Refresh the registration page and fill out the form using the `Short Password` data.
- **Action:** Attempt to click "Register".
- **Verify:** The browser's native HTML5 validation immediately arrests the submit action, complaining that the password input must be at least 6 characters long.

---

## Pass/Fail Criteria

- **Pass:** The browser blocks submission of short passwords, and the backend explicitly kicks back duplicate emails with safe, correctly rendered error messages.
- **Fail:** If a 5-character password processes successfully, or if "Email already in use." displays raw code injections or creates a ghost account.

---

## Postconditions

- No new accounts are created.
- The user is left on the registration page without an active session.

---

## Related Cases

- [TC_ACC_001: Valid Account Registration](./TC_ACC_001_Registration_Happy_Path.md)
