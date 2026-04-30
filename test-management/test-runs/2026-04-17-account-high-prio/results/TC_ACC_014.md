# Missing Password Validation

## Execution Result

| Who | When | Result | Where | Comment |
| :--- | :--- | :--- | :--- | :--- |
| AI & User | 2026-04-17 | `⏳ PENDING` | General | |

Verifies that leaving the Password field blank prevents form submission and correctly highlights the missing field.

## Metadata

- **Test ID:** TC_ACC_014
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
> Both client-side and server-side validation should protect against missing passwords.

## Preconditions

- The Posters Demo Store is running.
- The user is on the Login page.

## Test Data

| Field | Value |
| :--- | :--- |
| Email | valid_user@posters.com |
| Password | (empty) |

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

### 1. Leave Password Empty

- **Action:** Enter a valid email address but leave the password field completely empty.
- **Data:** `Email` = `valid_user@posters.com`, `Password` = ` `
- **Verify:** The form is populated with only the email.

### 2. Submit Login Form

- **Action:** Click the "Login" button.
- **Verify:** The form is not submitted. A validation warning (e.g., "Please fill out this field") is displayed specifically indicating that the Password field is required.

### 3. Verify Both Fields Empty Behavior

- **Action:** Leave both the Email and Password fields empty, and click the "Login" button.
- **Verify:** The form is not submitted, and one or both of the required fields throw an immediate validation warning.

---

## Pass/Fail Criteria

- **Pass:** The form submission is blocked and the user is correctly informed that the password is required.
- **Fail:** The form is submitted to the backend or causes an unhandled error state.

---

## Postconditions

- The user remains unauthenticated on the Login page.

---

## Related Cases

- [TC_ACC_013: Missing Email Validation](./TC_ACC_013.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-04-09 | 1.0 | Antigravity (AI) | Initial creation |
