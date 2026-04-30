# Failed Login (Invalid Credentials)

Verifies that providing an unregistered email or incorrect password results in a standard login failure with an appropriate general error message.

## Metadata

- **Test ID:** TC_ACC_012
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🔴 Critical
- **Status:** ✅ Active
- **Execution Type:** Manual
- **Suite:** 🔄 Regression, 🧪 Full, 🔒 Security
- **Requirements:**
  - Login Error Handling
- **Tags:** `login`, `authentication`, `error-path`, `security`
- **Author:** Antigravity (AI) (2026-04-09)
- **Reviewers:**
  - AI Personas (BA, QA, Manager, Consumer) (2026-04-09)

## Comments

> [!CAUTION]
> The error message should be generic (e.g., "Invalid email or password") to prevent email/account enumeration attacks.

## Preconditions

- The Posters Demo Store is running.
- The user is on the Login page.

## Test Data

| Field | Value |
| :--- | :--- |
| Valid Email | valid_user@posters.com |
| Invalid Email | does_not_exist@posters.com |
| Valid Password| `S3cur3!P@ss` |
| Invalid Password | `Wr0ngP@ssw0rd!` |

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

### 1. Test Unregistered Email

- **Action:** Enter the `Invalid Email` and a password into the fields, then click "Login".
- **Data:** `Email` = `does_not_exist@posters.com`, `Password` = `AnyPassword123!`
- **Verify:** The login fails and a generic error message (e.g., "Invalid email or password") is clearly displayed to the user.

### 2. Test Incorrect Password

- **Action:** Enter the `Valid Email` but an `Invalid Password`, then click "Login".
- **Data:** `Email` = `valid_user@posters.com`, `Password` = `Wr0ngP@ssw0rd!`
- **Verify:** The login fails and the *same* generic error message used in Step 1 is displayed.

---

## Pass/Fail Criteria

- **Pass:** The system correctly denies access and displays a generic error message that doesn't reveal whether the email exists.
- **Fail:** Access is incorrectly granted, the system crashes, or the error message confirms/denies the existence of the email address (account enumeration vulnerability).

---

## Postconditions

- The user remains unauthenticated.

---

## Related Cases

- [TC_ACC_011: Successful Account Login](./TC_ACC_011.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-04-09 | 1.0 | Antigravity (AI) | Initial creation |
