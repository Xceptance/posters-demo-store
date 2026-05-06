# CSRF Protection on Registration

Verify that the registration form uses CSRF protection and correctly blocks requests without a valid token.

## Metadata

- **Test ID:** TC_SEC_002
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Customer, Security
- **Priority:** 🔴 Critical
- **Status:** ✅ Active
- **Execution Type:** Manual
- **Suite:** 🔒 Security
- **Requirements:**
  - SEC-007: CSRF Protection for Storefront
- **Tags:** `csrf`, `security`, `registration`
- **Author:** AI Agent (2026-05-06)
- **Reviewers:**

## Comments

This test manually simulates a CSRF attack on the registration form to ensure the backend rejects it.

## Preconditions

- The Posters Demo Store is running.

## Test Data

| Field | Value |
| :--- | :--- |
| Email | newtest@example.com |
| Password | Test123! |
| Confirm Password | Test123! |
| First Name | Jane |
| Last Name | Doe |

## Execution Targets

**Target Locales:**
- [x] EN-US

**Target Viewports:**
- [x] Desktop (Large)

---

## Steps

### 1. View Registration Form

- **Action:** Navigate to the registration page and inspect the form element in browser DevTools.
- **Verify:** The form contains a hidden input field named `_csrf` with a token value.

### 2. Simulate CSRF Attack (Missing Token)

- **Action:** Use a tool like cURL, Postman, or a modified HTML form to submit a POST request to `/en-US/register` with the test data but **without** the `_csrf` token.
- **Verify:** The request is rejected. You are redirected to `/error?reason=session-expired` or receive a 403 Forbidden status. The user account is NOT created.

---

## Pass/Fail Criteria

- **Pass:** The registration form includes a CSRF token and requests without a valid token are blocked.
- **Fail:** A POST request without a valid CSRF token successfully registers a new user.

---

## Related Cases

- [TC_SEC_001: CSRF Protection on Login](./TC_SEC_001.md)
