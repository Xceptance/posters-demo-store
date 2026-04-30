# Password Complexity Requirements Verification

## Execution Result

| Who | When | Result | Where | Comment |
| :--- | :--- | :--- | :--- | :--- |
| rschwietzke | 2026-04-09 | `✅ PASSED` | English, Test Env, v11.01 | |

Verifies that the registration form enforces minimum password complexity rules (e.g., minimum length, required characters) and displays appropriate error messages when those requirements are not met.

## Metadata

- **Test ID:** TC_ACC_007
- **Version:** 1.1
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🟠 High
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** 🔄 Regression, 🔒 Security
- **Requirements:**
  - Registration
- **Tags:** `registration`, `password`, `validation`, `security`
- **Author:** Antigravity (AI) (2026-04-09)
- **Reviewers:**
  - 

## Comments

> [!TIP]
> *Example Screenshot:*
> <!-- ![Create Account Form](../images/account/create-account.png) -->

## Preconditions

- The Posters Demo Store is running.
- The user is on the Account Creation page.
- The browser cart can be empty or have items.

## Test Data

| Field | Value |
| :--- | :--- |
| First Name | Jane |
| Last Name | Doe |
| Email | test_TC_ACC_007@posters.com |
| Password | `S3cur3!P@ss` |

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

### 1. Fill Valid Form Data
- **Action:** Enter valid First Name, Last Name, and Email.
- **Verify:** Fields accept input.

### 2. Test Short Password
- **Action:** Enter a short password, click "Create Account" or tab away.
- **Data:** `Password` = `pwd`
- **Verify:** An error message indicates the password is too short.

### 3. Test Weak Password
- **Action:** Enter a password without proper complexity (e.g., all lowercase).
- **Data:** `Password` = `password123`
- **Verify:** An error message indicates missing complexity requirements (e.g., missing uppercase, special character).

### 4. Provide Valid Password
- **Action:** Replace with a strong, valid password.
- **Data:** `Password` = `SecureP@ssw0rd!`
- **Verify:** Password passes validation and form can be submitted successfully.

---

## Pass/Fail Criteria

- **Pass:** Expected behavior matches the actual outcome for the scenario.
- **Fail:** System crashes, unexpected error pages, or validation bypassed.

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
