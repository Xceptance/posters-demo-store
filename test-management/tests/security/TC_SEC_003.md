# CSRF Protection on Checkout

Verify that the checkout forms use CSRF protection and correctly block requests without a valid token.

## Metadata

- **Test ID:** TC_SEC_003
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Checkout, Security
- **Priority:** 🔴 Critical
- **Status:** ✅ Active
- **Execution Type:** Manual
- **Suite:** 🔒 Security
- **Requirements:**
  - SEC-007: CSRF Protection for Storefront
- **Tags:** `csrf`, `security`, `checkout`
- **Author:** AI Agent (2026-05-06)
- **Reviewers:**

## Preconditions

- The Posters Demo Store is running.
- You have an item in your cart.

## Execution Targets

**Target Locales:**
- [x] EN-US

**Target Viewports:**
- [x] Desktop (Large)

---

## Steps

### 1. View Checkout Shipping Form

- **Action:** Proceed to checkout (Shipping Address step) and inspect the form element.
- **Verify:** The form contains a hidden input field named `_csrf` with a token value.

### 2. Simulate CSRF Attack (Shipping Step)

- **Action:** Submit a POST request to `/en-US/checkout/shippingAddress` with shipping data but **without** the `_csrf` token.
- **Verify:** The request is rejected (redirected to session expired error page or 403 status).

---

## Pass/Fail Criteria

- **Pass:** The checkout forms include a CSRF token and requests without a valid token are blocked.
- **Fail:** A POST request without a valid CSRF token successfully progresses the checkout flow.
