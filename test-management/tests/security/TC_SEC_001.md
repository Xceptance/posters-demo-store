# CSRF Protection on Login

Verify that the login form uses CSRF protection and correctly blocks requests without a valid token.

## Metadata

- **Test ID:** TC_SEC_001
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Customer, Security
- **Priority:** 🔴 Critical
- **Status:** ✅ Active
- **Execution Type:** Manual
- **Suite:** 🔒 Security
- **Requirements:**
  - SEC-007: CSRF Protection for Storefront
- **Tags:** `csrf`, `security`, `login`
- **Author:** AI Agent (2026-05-06)
- **Reviewers:**

## Comments

This test manually simulates a CSRF attack on the login form to ensure the backend rejects it.

## Preconditions

- The Posters Demo Store is running.
- You have a registered user account.

## Test Data

| Field | Value |
| :--- | :--- |
| Email | test@example.com |
| Password | Test123! |

## Execution Targets

**Target Locales:**
- [x] EN-US
- [ ] DE-DE

**Target Viewports:**
- [x] Desktop (Large)

---

## Steps

### 1. View Login Form

- **Action:** Navigate to the login page and inspect the form element in browser DevTools.
- **Verify:** The form contains a hidden input field named `_csrf` with a token value.

### 2. Successful Login

- **Action:** Enter valid credentials and submit the form normally.
- **Verify:** Login succeeds and redirects to the homepage or account overview.

### 3. Simulate CSRF Attack (Missing Token)

- **Action:** 
  1. Log out.
  2. Use a tool like cURL, Postman, or a modified HTML form to submit a POST request to `/en-US/login` with credentials but **without** the `_csrf` token.
- **Verify:** The request is rejected. You are redirected to `/error?reason=session-expired` or receive a 403 Forbidden status.

### 4. Simulate CSRF Attack (Invalid Token)

- **Action:** Submit the same POST request but with an invalid or tampered `_csrf` token (e.g., `_csrf=invalid-token-123`).
- **Verify:** The request is rejected exactly as in step 3.

---

## Pass/Fail Criteria

- **Pass:** The login form includes a CSRF token, successful logins work, and requests without a valid token are blocked.
- **Fail:** A POST request without a valid CSRF token successfully logs the user in.

---

## Postconditions

- User is logged out.

---

## Related Cases

- [TC_SEC_002: CSRF Protection on Registration](./TC_SEC_002.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-05-06 | 1.0 | AI Agent | Initial creation |
