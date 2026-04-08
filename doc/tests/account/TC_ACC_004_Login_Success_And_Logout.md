# Account Login and Session Teardown

This test checks the core authentication cycle: validating correct credentials, initiating a server-side session, establishing the user context in the browser, and cleanly tearing down that session on logout.

## Metadata

- **Test ID:** TC_ACC_004
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🔴 Critical
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** 🚀 Smoke, 🔄 Regression, 🔒 Security
- **Requirements:**
  - [REQ-ID or link to backlog item]
- **Tags:** `account`, `login`, `logout`, `session`
- **Author:** Antigravity (AI) (2026-04-08)
- **Reviewers:**
  - [Human]

## Comments

> [!NOTE]
> Sessions in this environment are dictated by `SessionService` which tags the HTTP Session with the `customerId`. 

## Preconditions

- The Posters Demo Store is running.
- A registered user exists within the database (Run `TC_ACC_001` first).

## Test Data

| Data ID | Email | Password |
| :--- | :--- | :--- |
| `Valid Credentials` | `johndoe.test@example.com` | `testpass123` |

## Execution Targets

**Target Locales:**
- [x] EN-US
- [x] SV-SE

**Target Viewports:**
- [x] Desktop (1920x1080)
- [x] Mobile (375x812)

## Steps

### 1. Initiate Login
- **Action:** Open the home page and click the "Login" utility link.
- **Verify:** The user is taken to `/{locale}/login`. Emptied email and password fields are displayed.

### 2. Enter Valid Credentials
- **Action:** Fill out the forms using `Valid Credentials`. 
- **Action:** Click "Login" (or Submit).
- **Verify:** The user is immediately redirected to the Home page (`/{locale}/`).
- **Verify:** The header correctly acknowledges the authentication (e.g. showing Logout/Account links instead of Guest links).

### 3. Finalize Logout
- **Action:** Click the "Logout" header link (directed through `/{locale}/logout`).
- **Verify:** The user is redirected back to the Home page organically.
- **Verify:** The session entirely tears down, replacing the account navigation buttons with "Login" and "Create Account" links.

---

## Pass/Fail Criteria

- **Pass:** The session is faithfully assigned via correct credentials and is irreparably wiped clean upon hitting the `/logout` endpoint, proven by the header UI swapping accurately.
- **Fail:** If a user remains logged in after hitting Logout, or if valid credentials trigger an endless loop.

---

## Postconditions

- The user operates on a cleared, completely unauthenticated guest session.

---

## Related Cases

- [TC_ACC_005: Login Failure Handling](./TC_ACC_005_Login_Failure_Handling.md)
- [TC_ACC_007: Protected Routes Redirection](./TC_ACC_007_Protected_Routes_Redirection.md)
