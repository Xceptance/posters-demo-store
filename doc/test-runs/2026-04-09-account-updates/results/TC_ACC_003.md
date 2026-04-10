# Invalid Email Format Validation

## Execution Result

| Who | When | Result | Where | Comment |
| :--- | :--- | :--- | :--- | :--- |
| rschwietzke | 2026-04-09 | `✅ PASSED` | English, Test Env, v11.01 | |

Verifies that incorrectly formatted email strings trigger a validation error during registration.

## Metadata

- **Test ID:** TC_ACC_003
- **Version:** 1.1
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🟡 Medium
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** 🔄 Regression
- **Requirements:**
  - Registration
- **Tags:** `registration`, `validation`, `email`
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
| Email | test_TC_ACC_003@posters.com |
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

### 1. Enter Invalid Email
- **Action:** Enter valid names and password, but enter an invalid email format.
- **Data:** `Email` = `jane.doe.posters` (no @ symbol or TLD)
- **Verify:** Inline validation or submission error indicates the email format is invalid.

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
