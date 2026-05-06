# CSRF Token in HTMX Requests

Verify that the global HTMX event listener correctly injects the CSRF token from meta tags into request headers.

## Metadata

- **Test ID:** TC_SEC_005
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Frontend, Security
- **Priority:** 🔴 Critical
- **Status:** ✅ Active
- **Execution Type:** Manual
- **Suite:** 🔒 Security
- **Requirements:**
  - SEC-007: CSRF Protection for Storefront
- **Tags:** `csrf`, `security`, `htmx`
- **Author:** AI Agent (2026-05-06)
- **Reviewers:**

## Preconditions

- The Posters Demo Store is running.
- You are viewing any storefront page.

## Steps

### 1. Verify Meta Tags

- **Action:** Inspect the HTML source of the page (`<head>`).
- **Verify:** The `<meta name="_csrf">` and `<meta name="_csrf_header">` tags are present and populated with token strings.

### 2. Verify HTMX Header Injection

- **Action:** Perform an HTMX action (like adding an item to the cart or updating cart quantity). Inspect the request in the Network tab of DevTools.
- **Verify:** The request headers include `X-CSRF-TOKEN` (or whatever the header meta tag specifies) matching the token from the meta tag.

---

## Pass/Fail Criteria

- **Pass:** HTMX requests automatically include the correct CSRF header sourced from the page's meta tags.
- **Fail:** The meta tags are missing, or HTMX requests are sent without the CSRF header.
