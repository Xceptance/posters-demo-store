# Duplicate Account Registration Attempt

Verifies that a user cannot register a new account with an email address already registered in the system.

## Metadata

- **Test ID:** TC_ACC_004
- **Version:** 1.4
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🔴 Critical
- **Status:** ✅ Active
- **Execution Type:** Automated
- **Suite:** 🔄 Regression
- **Requirements:**
  - Registration
- **Tags:** `registration`, `duplicate`, `security`
- **Author:** Antigravity (AI) (2026-04-09)
- **Reviewers:**
  - Antigravity (AI) (2026-05-21)

## Comments

> [!TIP]
> *Example Screenshot:*
> <!-- ![Create Account Form](../images/account/create-account.png) -->

## Preconditions

- The Posters Demo Store is running.
- The user is on the Account Creation page.
- A registered user account with the email `test_TC_ACC_004_existing@posters.com` exists in the system (automated scripts must register this account prior to executing this test).
- The browser cart can be empty or have items.

## Test Data

| Field | Value |
| :--- | :--- |
| First Name | Jane |
| Last Name | Doe |
| Existing Email | `test_TC_ACC_004_existing@posters.com` |
| Password | `S3cur3!P@ss` |

## Execution Targets

**Target Locales:**
- [x] EN-US
- [x] EN-GB
- [x] DE-DE
- [x] SV-SE
- [x] JA-JP

**Target Viewports:**
- [x] Desktop (Large)
- [x] Mobile (Small)

---

## Steps

### 1. Fill Form with Existing Email
- **Action:** Fill all fields with valid data from the Test Data table, using `test_TC_ACC_004_existing@posters.com` for the Email field.
- **Verify:** Inputs are accepted.

### 2. Submit Form
- **Action:** Click "Create Account".
- **Verify:** Registration is rejected.
- **Verify:** The user remains on the Account Creation page, the inputs remain populated (except Password which may clear), and a validation error message appears indicating the email is already in use (e.g. *"An account with this email address already exists."*).

---

## Pass/Fail Criteria

- **Pass:** The registration submission is blocked when using a duplicate email, and a clear error message is displayed indicating the email is already in use.
- **Fail:** System crashes, unexpected error pages, duplicate account is successfully created, or no error message is shown to the user.

---

## Postconditions

- Session state may be authenticated depending on success.

---

## Related Cases

- [TC_ACC_001: Successful Account Registration](./TC_ACC_001.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-04-09 | 1.0 | Antigravity (AI) | Initial creation |
| 2026-04-09 | 1.1 | Antigravity (AI) | Added EN-GB and SV-SE to target locales |
| 2026-05-21 | 1.2 | Antigravity (AI) | Parameterized existing duplicate email, defined self-contained registration precondition, added 'tobeautomated' tag, and promoted to Active. |
| 2026-05-21 | 1.3 | Gemini 2.5 Pro | Automated the test case and removed tobeautomated tag |
| 2026-05-21 | 1.4 | Gemini 3.5 Flash | Added JA-JP locale |
