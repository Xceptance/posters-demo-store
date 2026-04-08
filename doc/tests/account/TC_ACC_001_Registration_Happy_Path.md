# Valid Account Registration

This test validates the standard registration flow, ensuring a user can successfully create an account, is logged securely, and is immediately redirected to the storefront as a signed-in user.

## Metadata

- **Test ID:** TC_ACC_001
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🔴 Critical
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** 🚀 Smoke, 🔄 Regression
- **Requirements:**
  - [REQ-ID or link to backlog item]
- **Tags:** `account`, `registration`, `happy-path`
- **Author:** Antigravity (AI) (2026-04-08)
- **Reviewers:**
  - [Human]

## Comments

> [!NOTE]
> The Postconditions of this test establish the session state required for tests like **TC_ACC_006**.

## Preconditions

- The Posters Demo Store is running.
- The user is currently browsing as a guest (no active session).

## Test Data

| Data ID | First Name | Last Name | Email | Password |
| :--- | :--- | :--- | :--- | :--- |
| `Valid Account` | John | Doe | `johndoe.test@example.com` | `testpass123` |

## Execution Targets

**Target Locales:**
- [x] EN-US
- [x] DE-DE
- [x] SV-SE

**Target Viewports:**
- [x] Desktop (1920x1080)
- [x] Mobile (375x812)

## Steps

### 1. Navigate to Registration page
- **Action:** Open the home page and click the "Create Account" link (or navigate to `/{locale}/register`).
- **Verify:** The registration form is displayed with fields for First Name, Last Name, Email, and Password.

### 2. Enter Valid Details
- **Action:** Fill the Registration form with the data mapped in `Valid Account`.
- **Verify:** Form accepts the inputs without inline validation errors.

### 3. Submit Registration
- **Action:** Click the "Register" submit button.
- **Verify:** The browser redirects immediately to the home page (`/{locale}/`).

### 4. Verify Active Session
- **Action:** Observe the global navigation header.
- **Verify:** The header correctly drops the "Login / Register" guest links and displays account management paths (or a logged-in state greeting like "Account Overview").

---

## Pass/Fail Criteria

- **Pass:** The user inputs valid details, submits the form, and is immediately redirected to the homepage with a validated session visible in the header.
- **Fail:** Form submission crashes (500 error), redirect lands on an incorrect page, or the session is not visibly established.

---

## Postconditions

- User profile for `johndoe.test@example.com` exists in the database.
- An authenticated web session is actively running within the browser.

---

## Related Cases

- [TC_ACC_004: Login Success and Logout](./TC_ACC_004_Login_Success_And_Logout.md)
- [TC_ACC_006: Account Overview and Update](./TC_ACC_006_Account_Overview_And_Update.md)
