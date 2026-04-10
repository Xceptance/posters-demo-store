# Robust Email Input Processing

## Execution Result

| Who | When | Result | Where | Comment |
| :--- | :--- | :--- | :--- | :--- |
| rschwietzke | 2026-04-09 | `✅ PASSED` | English, Test Env, v11.01 | |

Verifies that the registration form handles padded whitespace appropriately and treats emails case-insensitively. This prevents creation of duplicate accounts (e.g. `User@email` vs `user@email`) and whitespace-related login errors.

## Metadata

- **Test ID:** TC_ACC_008
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🟠 High
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** 🔄 Regression
- **Requirements:**
  - Registration
- **Tags:** `registration`, `validation`, `email`, `edge-case`
- **Author:** Antigravity (AI) (2026-04-09)
- **Reviewers:**
  - 

## Comments

> [!CAUTION]
> It is extremely common for users to copy/paste emails from mobile devices, introducing trailing spaces. Ensure the application trims spaces before doing uniqueness checks or saving.

## Preconditions

- The Posters Demo Store is running.
- The user is on the Account Creation page.

## Test Data

| Field | Value |
| :--- | :--- |
| First Name | Emma |
| Last Name | Watson |
| Email with Spaces | ` emma@posters.com ` |
| Email Case Variation | `Emma@posters.com` |
| Valid Password | `S3cureP@ss!` |

## Execution Targets

**Target Locales:**
- [x] EN-US
- [x] EN-GB
- [x] DE-DE
- [x] SV-SE

**Target Viewports:**
- [x] Desktop (Large)
- [x] Mobile (Small)

---

## Steps

### 1. Register with Space-Padded Email
- **Action:** Fill form with valid data, using space-padded email. Submitting the form.
- **Data:** `Email` = ` emma@posters.com `
- **Verify:** Registration is successful and the email is saved as `emma@posters.com`.

### 2. Verify Case Variation Duplicate
- **Action:** Navigate back to registration. Attempt to register again using the capitalized version.
- **Data:** `Email` = `Emma@posters.com`
- **Verify:** System rejects the registration, indicating the email is already in use.

---

## Pass/Fail Criteria

- **Pass:** The system trims spaces during registration and treats uppercase/lowercase email strings identically for account uniqueness matching.
- **Fail:** Accounts with trailing spaces are saved literally, or multiple accounts can be created differing only by case.

---

## Postconditions

- A test account `emma@posters.com` may exist.

---

## Related Cases

- [TC_ACC_001: Successful Account Registration](./TC_ACC_001.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-04-09 | 1.0 | Antigravity (AI) | Initial creation |
