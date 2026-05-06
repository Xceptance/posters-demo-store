# Successful Account Login

## Execution Result

| Who | When | Result | Where | Comment |
| :--- | :--- | :--- | :--- | :--- |
| AI Copilot | 2026-05-06 | `✅ PASSED` | Local Environment | Landed on homepage. Added to the related improvement request. |

Verifies that a user can successfully log in using a valid, registered email and password.

## Metadata

- **Test ID:** TC_ACC_011
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🔴 Critical
- **Status:** ✅ Active
- **Execution Type:** Manual
- **Suite:** 🚀 Smoke, 🔄 Regression, 🧪 Full
- **Requirements:**
  - Login Flow
- **Tags:** `login`, `authentication`, `happy-path`
- **Author:** Antigravity (AI) (2026-04-09)
- **Reviewers:**
  - AI Personas (BA, QA, Manager, Consumer) (2026-04-09)

## Comments

> [!TIP]
> This is the primary verification that the authentication system is functional.

## Preconditions

- The Posters Demo Store is running.
- The user is on the Login page.
- A user account exists with known credentials (e.g., from TC_ACC_001).

## Test Data

| Field | Value |
| :--- | :--- |
| Email | valid_user@posters.com |
| Password | `S3cur3!P@ss` |

## Execution Targets

**Target Locales:**
- [x] EN-US
- [x] EN-GB
- [x] DE-DE
- [x] SV-SE

**Target Viewports:**
- [x] Desktop (Large)
- [x] Tablet (Medium)
- [x] Mobile (Small)

---

## Steps

### [x] 1. Fill Login Credentials

- **Action:** Enter the valid `Email` and `Password` into the respective fields.
- **Data:** `Email` = `valid_user@posters.com`, `Password` = `S3cur3!P@ss`
- **Verify:** The fields are populated correctly with the entered data.

### [x] 2. Submit Login Form

- **Action:** Click the "Login" button.
- **Verify:** The system authenticates the user and redirects them to the Account Dashboard or homepage (depending on initial navigation). The user session is now active.

---

## Pass/Fail Criteria

- **Pass:** The user is successfully authenticated and redirected without error messages.
- **Fail:** The login is rejected, an error message is displayed, or the page crashes.

---

## Postconditions

- The user is authenticated and an active session exists.

---

## Related Cases

- [TC_ACC_001: Successful Account Registration](./TC_ACC_001.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-04-09 | 1.0 | Antigravity (AI) | Initial creation |
