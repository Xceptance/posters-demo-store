# Authentication Rejection and Failure Handling

This test guarantees that incorrect combinations of email and passwords securely reject the end user and return safe, un-escaped error strings to the UI, explicitly avoiding enumeration vectors where possible.

## Metadata

- **Test ID:** TC_ACC_005
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🔴 Critical
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** 🔄 Regression, 🔒 Security
- **Requirements:**
  - [REQ-ID or link to backlog item]
- **Tags:** `account`, `login`, `negative`, `validation`
- **Author:** Antigravity (AI) (2026-04-08)
- **Reviewers:**
  - [Human]

## Comments

> [!CAUTION]
> A critical failure point here is if the application returns a different error for "Wrong Password" versus "Email does not exist", as this causes account enumeration. The response must be generic.

## Preconditions

- The Posters Demo Store is running.
- The user `johndoe.test@example.com` exists in the database.

## Test Data

| Data ID | Email | Password | Target Issue |
| :--- | :--- | :--- | :--- |
| `Wrong Password` | `johndoe.test@example.com` | `invalidPass123` | Active Account, Bad DB Hash Match |
| `Unknown Email` | `nobody.ever@example.com` | `testpass123` | No DB Entity Match |

## Execution Targets

**Target Locales:**
- [x] EN-US
- [x] DE-DE

**Target Viewports:**
- [x] Desktop (1920x1080)

## Steps

### 1. Test Incorrect Password Check
- **Action:** Open `/{locale}/login` and submit the credentials for `Wrong Password`.
- **Verify:** The session is rejected and the user is redirected immediately back to the `/{locale}/login` interface.
- **Verify:** A flash error attribute correctly displays standard text: "Invalid email or password." (or precisely identical locale translation).

### 2. Test Invalid Email Validation
- **Action:** Submit the credentials mapped to `Unknown Email`.
- **Verify:** The system intercepts the nonexistent entity retrieval, redirects to `/{locale}/login`.
- **Verify:** The flash error attribute identically matches Step 1 ("Invalid email or password.").

---

## Pass/Fail Criteria

- **Pass:** The endpoints strictly return exactly identical messages preventing credential discovery sequences.
- **Fail:** If the system explicitly alerts "User not found" or "Incorrect password", or crashes into an unhandled Java internal server error (HTTP 500).

---

## Postconditions

- The browser maintains a purely unauthenticated session.

---

## Related Cases

- [TC_ACC_004: Valid Account Login](./TC_ACC_004_Login_Success_And_Logout.md)
