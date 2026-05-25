# Mandatory Fields Validation

Verifies that empty form submissions are blocked and display field-level validation errors.

## Metadata

- **Test ID:** TC_ACC_002
- **Version:** 1.4
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🟠 High
- **Status:** ✅ Active
- **Execution Type:** Automated
- **Suite:** 🔄 Regression
- **Requirements:**
  - Registration
- **Tags:** `registration`, `validation`
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
| Email | test_TC_ACC_002@posters.com |
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

### 1. Submit Empty Form
- **Action:** Open the create account page. Click "Create Account" without filling in any data.
- **Verify:** Form submission is prevented.
- **Verify [Visual]:** A browser validation error overlay "Please fill out this field." appears pointing to the "First Name" input field.

### 2. Test Single Field Omissions
- **Action:** Fill three of the four required fields (First Name, Last Name, Email, Password) with valid data from the Test Data table, leaving exactly one field blank, and click "Create Account". Repeat this process for each of the four required fields.
- **Verify:** Form submission is blocked each time.
- **Verify [Visual]:** The browser validation overlay "Please fill out this field." appears specifically for the omitted field.

---

## Pass/Fail Criteria

- **Pass:** The form submission is blocked whenever any required field is left empty, and the browser displays the validation error overlay "Please fill out this field." for the missing field.
- **Fail:** System crashes, unexpected error pages, form submits successfully with empty required fields, or no validation overlay is displayed.

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
| 2026-05-21 | 1.2 | Antigravity (AI) | Refined steps to test single-field omissions, specified browser overlay message 'Please fill out this field.', added 'tobeautomated' tag, and promoted to Active. |
| 2026-05-21 | 1.3 | Gemini 2.5 Pro | Automated the test case and removed tobeautomated tag |
| 2026-05-21 | 1.4 | Gemini 3.5 Flash | Added JA-JP locale |
