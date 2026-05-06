# API Endpoints Excluded from CSRF

Verify that specified stateless API endpoints (e.g., `/api/v2/**`) do not require a CSRF token.

## Metadata

- **Test ID:** TC_SEC_008
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** API, Security
- **Priority:** 🟡 Medium
- **Status:** ✅ Active
- **Execution Type:** Manual
- **Suite:** 🔒 Security
- **Requirements:**
  - SEC-007: CSRF Protection for Storefront
- **Tags:** `csrf`, `security`, `api`
- **Author:** AI Agent (2026-05-06)
- **Reviewers:**

## Preconditions

- The Posters Demo Store is running.

## Steps

### 1. Make API POST Request

- **Action:** Use an API client (like Postman or cURL) to make a POST request to an API endpoint such as `/api/v2/cart/add` with valid payload but **without** a CSRF token.
- **Verify:** The request is NOT blocked by a 403 Forbidden CSRF error. It may return a 400 Bad Request or 200 OK depending on the payload validity, which indicates the CSRF filter correctly ignored the path.

---

## Pass/Fail Criteria

- **Pass:** The `/api/v2/**` endpoints can be successfully accessed via POST without a CSRF token.
- **Fail:** The `/api/v2/**` endpoints reject requests with a 403 Forbidden due to missing CSRF token.
