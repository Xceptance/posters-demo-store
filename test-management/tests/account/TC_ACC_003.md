# Invalid Email Format Validation

Verifies that incorrectly formatted email strings trigger a validation error during registration.

## Metadata

- **Test ID:** TC_ACC_003
- **Version:** 1.4
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🟡 Medium
- **Status:** ✅ Active
- **Execution Type:** Automated
- **Suite:** 🔄 Regression
- **Requirements:**
  - Registration
- **Tags:** `registration`, `validation`, `email`
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
- The browser cart can be empty or have items.

## Test Data

| Field | Value |
| :--- | :--- |
| First Name | Jane |
| Last Name | Doe |
| Password | `S3cur3!P@ss` |

**Invalid Email Partitions:**
- **Missing `@`:** `janedoeposters.com`
- **Missing local part:** `@posters.com`
- **Missing domain:** `jane@`
- **Multiple `@` symbols:** `jane@@posters.com`
- **Invalid domain characters:** `jane@post#ers.com`

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

### 1. Fill Valid Form Names & Password
- **Action:** Open the create account page. Enter valid First Name, Last Name, and Password values from the Test Data table.
- **Verify:** Fields accept input successfully.

### 2. Enter and Verify Invalid Emails
- **Action:** For each email in the **Invalid Email Partitions** table:
  1. Input the invalid email value into the Email field.
  2. Click the "Create Account" button.
- **Verify:** Form submission is blocked.
- **Verify [Visual]:** A browser-native validation error overlay appears pointing to the Email field, indicating that the email address is invalid or missing required symbols (e.g., *"Please include an '@'..."*).

---

## Pass/Fail Criteria

- **Pass:** The registration form blocks submission for all invalid email formats, and the browser displays a native validation overlay indicating the format error.
- **Fail:** System crashes, unexpected error pages, the form successfully submits with an invalid email format, or no validation overlay is displayed.

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
| 2026-05-21 | 1.2 | Antigravity (AI) | Expanded invalid email test data to cover multiple EP partitions, specified browser validation behavior, added 'tobeautomated' tag, and promoted to Active. |
| 2026-05-21 | 1.3 | Gemini 2.5 Pro | Automated the test case and removed tobeautomated tag |
| 2026-05-21 | 1.4 | Gemini 3.5 Flash | Added JA-JP locale |
