# Missing Email Validation

Verifies that leaving the Email field blank prevents form submission and correctly highlights the missing field.

## Metadata

- **Test ID:** TC_ACC_013
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🟠 High
- **Status:** ✅ Active
- **Execution Type:** Manual
- **Suite:** 🔄 Regression, 🧪 Full
- **Requirements:**
  - Login HTML5 Validation
- **Tags:** `login`, `validation`, `empty-fields`
- **Author:** Antigravity (AI) (2026-04-09)
- **Reviewers:**
  - AI Personas (BA, QA, Manager, Consumer) (2026-04-09)

## Comments

> [!TIP]
> HTML5 client-side validation should catch this before a server request is even made.

## Preconditions

- The Posters Demo Store is running.
- The user is on the Login page.

## Test Data

| Field | Value |
| :--- | :--- |
| Email | (empty) |
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

### 1. Leave Email Empty

- **Action:** Ensure the Email field is completely empty. Enter a value into the Password field.
- **Data:** `Email` = ` `, `Password` = `S3cur3!P@ss`
- **Verify:** The form is populated with only the password.

### 2. Submit Login Form

- **Action:** Click the "Login" button.
- **Verify:** The form is not submitted. The browser or application displays a validation warning (e.g., "Please fill out this field") specifically indicating that the Email field is required.

---

## Pass/Fail Criteria

- **Pass:** The form submission is blocked and the user is correctly informed that the email is required.
- **Fail:** The form submits to the server resulting in a 500 or standard application error page instead of a handled validation message.

---

## Postconditions

- The user remains unauthenticated on the Login page.

---

## Related Cases

- [TC_ACC_014: Missing Password Validation](./TC_ACC_014.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-04-09 | 1.0 | Antigravity (AI) | Initial creation |
