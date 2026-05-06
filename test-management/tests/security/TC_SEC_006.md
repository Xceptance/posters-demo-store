# Session Timeout Handling

Verify that a session timeout results in a user-friendly error page instead of a generic 403 when submitting a form.

## Metadata

- **Test ID:** TC_SEC_006
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** UX, Security
- **Priority:** 🟡 Medium
- **Status:** ✅ Active
- **Execution Type:** Manual
- **Suite:** 🔒 Security
- **Requirements:**
  - SEC-007: CSRF Protection for Storefront
- **Tags:** `csrf`, `security`, `timeout`
- **Author:** AI Agent (2026-05-06)
- **Reviewers:**

## Preconditions

- The Posters Demo Store is running.

## Steps

### 1. Let Session Expire

- **Action:** Open the login page. Wait for the session to expire (or artificially delete the `JSESSIONID` cookie using DevTools).
- **Verify:** The session is considered expired.

### 2. Submit Form

- **Action:** Fill out the login form and submit it.
- **Verify:** You are redirected to a user-friendly error page (`/error?reason=session-expired`) indicating that the session has expired, rather than a generic 403 Forbidden page. The page should offer a "Refresh" or "Home" button.

---

## Pass/Fail Criteria

- **Pass:** Submitting a form after session expiration displays a friendly error page.
- **Fail:** Submitting a form after session expiration results in an unhandled exception or generic web server 403 error page.
