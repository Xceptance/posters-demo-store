# Successful Account Registration

Verifies that a new user can successfully create an account when all fields are filled with valid data.

## Metadata

- **Test ID:** TC_ACC_001
- **Version:** 1.7
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🔴 Critical
- **Status:** ✅ Active
- **Execution Type:** Automated
- **Suite:** 🚀 Smoke, 🔄 Regression
- **Requirements:**
  - Registration
- **Tags:** `account`, `registration`, `happy-path`
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
| Email | A unique dynamic address (e.g. `test_TC_ACC_001_${TIMESTAMP}@posters.com` or `test_TC_ACC_001_${RANDOM_ID}@posters.com`) to prevent registration collision |
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

### 1. Launch Create Account
- **Action:** Open the create account page.
- **Verify [Visual]:** The Create Account form is horizontally centered on the page.
- **Verify [Visual]:** The entire form is styled as a white card-style container with subtle borders/shadows (similar to the checkout address cards).
- **Verify:** The form contains all four input fields (First Name, Last Name, Email, and Password).
- **Verify:** A link to direct existing users ("Already have an account?" or similar) is present on the form.
- **Verify [Visual]:** The action/submission buttons (e.g., "Create Account") are right-aligned within the form/card layout.

### 2. Enter Valid Data
- **Action:** Fill all fields with valid data.
- **Data:** `First Name` = `Jane`, `Last Name` = `Doe`, `Email` = A unique generated dynamic email (e.g., using timestamp or random string), `Password` = `S3cur3!P@ss`
- **Verify:** No inline validation errors are shown.

### 3. Submit Form
- **Action:** Click "Create Account".
- **Verify:** The user is redirected to the home page (URL path is `/`).
- **Verify:** A success alert/message "Your account has been created." is displayed.
- **Verify:** The header navigation updates: the "Sign In" link is replaced by "Logout", and a greeting "Hello, Jane" is visible.

### 4. Cross-Check Account Persistence
- **Action:** Click "Logout" to terminate the registered session. Navigate back to the "Sign In" page, input the registered email and password, and submit the login form.
- **Data:** `Email` = The unique email created in Step 2, `Password` = `S3cur3!P@ss`
- **Verify:** The user is successfully logged in.
- **Verify:** The user is redirected to the home page, and the header displays "Hello, Jane" with the "Logout" option visible.

---

## Pass/Fail Criteria

- **Pass:** The registration form successfully submits valid data, the user is redirected to the homepage, logged in automatically, and a subsequent logout and login using those same credentials completes successfully, confirming account persistence.
- **Fail:** System crashes, validation errors on registration, auto-login failure, or the subsequent login attempt fails (confirming the account was not correctly persisted).

---

## Postconditions

- The user is authenticated under the new account session.
- The newly created account exists in the database and can be used for future logins.

---

## Related Cases

- [TC_ACC_002: Mandatory Fields Validation](./TC_ACC_002.md)
- [TC_ACC_011: Successful Account Login](./TC_ACC_011.md)
- [TC_ACC_018: Successful Account Logout](./TC_ACC_018.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-04-09 | 1.0 | Antigravity (AI) | Initial creation |
| 2026-04-09 | 1.1 | Antigravity (AI) | Added EN-GB and SV-SE to target locales |
| 2026-05-21 | 1.2 | Antigravity (AI) | Refined verification steps, updated related cases, and set status to To Be Reviewed. |
| 2026-05-21 | 1.3 | Antigravity (AI) | Added layout verification for centering, card style, "already has account" link, and right-aligned buttons in Step 1. |
| 2026-05-21 | 1.4 | Antigravity (AI) | Refined email test data format to be dynamic, separated visual from functional verifications, marked as Automated, and added a Logout-Login persistence cross-check step. |
| 2026-05-21 | 1.5 | Antigravity (AI) | Corrected execution type back to Manual and labelled with 'tobeautomated' tag as requested. |
| 2026-05-21 | 1.6 | Gemini 2.5 Pro | Automated the test case and removed tobeautomated tag |
| 2026-05-21 | 1.7 | Gemini 3.5 Flash | Added JA-JP locale |
