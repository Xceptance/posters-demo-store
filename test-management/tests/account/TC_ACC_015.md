# Invalid Login - Swapped Credentials

Verifies that the login fails cleanly when a user accidentally swaps their email and password in the input fields.

## Metadata

- **Test ID:** TC_ACC_015
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🟡 Medium
- **Status:** ✅ Active
- **Execution Type:** Manual
- **Suite:** 🔄 Regression, 🧪 Full
- **Requirements:**
  - Login Field Validation
- **Tags:** `login`, `validation`, `edge-case`
- **Author:** Antigravity (AI) (2026-04-09)
- **Reviewers:**
  - AI Personas (BA, QA, Manager, Consumer) (2026-04-09)

## Comments

> [!TIP]
> The HTML5 input type `email` should catch the password value placed in the email field if it doesn't contain an `@` symbol.

## Preconditions

- The Posters Demo Store is running.
- The user is on the Login page.

## Test Data

| Field | Value |
| :--- | :--- |
| Email Input | `S3cur3!P@ss` |
| Password Input | `valid_user@posters.com` |

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

### 1. Enter Swapped Credentials

- **Action:** Enter the password into the Email field, and the email into the Password field.
- **Data:** `Email` = `S3cur3!P@ss`, `Password` = `valid_user@posters.com`
- **Verify:** Fields accept the input.

### 2. Submit Login Form

- **Action:** Click the "Login" button.
- **Verify:** The form fails to submit either via client-side HTML5 validation (e.g., "Please include an '@' in the email address", unless the password has an `@`), or via a generic login error message from the backend. The user is not granted access.

---

## Pass/Fail Criteria

- **Pass:** The login attempt fails gracefully and the user is not authenticated.
- **Fail:** The frontend fails with an unhandled exception, or backend processes the error poorly resulting in a 500 status code.

---

## Postconditions

- The user remains unauthenticated on the Login page.

---

## Related Cases

- [TC_ACC_012: Failed Login (Invalid Credentials)](./TC_ACC_012.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-04-09 | 1.0 | Antigravity (AI) | Initial creation |
