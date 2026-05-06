# CSRF Attack Simulation

Verify that the application blocks explicitly constructed cross-origin requests targeting state-changing endpoints.

## Metadata

- **Test ID:** TC_SEC_007
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Security
- **Priority:** 🔴 Critical
- **Status:** ✅ Active
- **Execution Type:** Manual
- **Suite:** 🔒 Security
- **Requirements:**
  - SEC-007: CSRF Protection for Storefront
- **Tags:** `csrf`, `security`, `attack`
- **Author:** AI Agent (2026-05-06)
- **Reviewers:**

## Preconditions

- The Posters Demo Store is running on `localhost:8080`.
- User is logged into the store in their primary browser.

## Steps

### 1. Simulate Attack Page

- **Action:** Create a local HTML file (e.g., `attack.html`) containing a form that auto-submits a POST request to `http://localhost:8080/en-US/accountOverview` with dummy profile update data. Do not include a CSRF token.
- **Verify:** The HTML file is ready.

### 2. Execute Attack

- **Action:** Open `attack.html` in the SAME browser where the user is logged into the store.
- **Verify:** The browser submits the request (sending the user's session cookies), but the server rejects the request (403 Forbidden or session expired redirect) because the CSRF token is missing/invalid. The user's profile is NOT updated.

---

## Pass/Fail Criteria

- **Pass:** The attack request is rejected and state remains unchanged.
- **Fail:** The attack request succeeds and modifies user data.
