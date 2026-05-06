# CSRF Protection on Cart Operations

Verify that cart modifications via HTMX include the CSRF token and server rejects invalid ones.

## Metadata

- **Test ID:** TC_SEC_004
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Cart, Security
- **Priority:** 🔴 Critical
- **Status:** ✅ Active
- **Execution Type:** Manual
- **Suite:** 🔒 Security
- **Requirements:**
  - SEC-007: CSRF Protection for Storefront
- **Tags:** `csrf`, `security`, `cart`
- **Author:** AI Agent (2026-05-06)
- **Reviewers:**

## Preconditions

- The Posters Demo Store is running.
- You are viewing a product detail page.

## Steps

### 1. Add to Cart

- **Action:** Click the "Add to Cart" button. Inspect the network request in DevTools.
- **Verify:** The HTMX request includes the `X-CSRF-TOKEN` header. The request succeeds.

### 2. Simulate CSRF Attack (Cart Update)

- **Action:** Use an external tool to POST to `/en-US/updateProductCount` (or relevant cart update endpoint) without the CSRF header or hidden field.
- **Verify:** The request is blocked (403 Forbidden or redirected to error page). The cart is not updated.

---

## Pass/Fail Criteria

- **Pass:** Cart operations include CSRF headers and manual requests without them are blocked.
- **Fail:** Cart can be updated via POST request without a valid CSRF token.
