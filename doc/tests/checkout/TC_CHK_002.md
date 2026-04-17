# Registered User Checkout

A logged-in user with a saved address and payment method completes the checkout process, leveraging their pre-filled information for a faster experience.

## Metadata

- **Test ID:** TC_CHK_002
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Checkout, Account, Cart
- **Priority:** 🔴 Critical
- **Status:** 👀 To Be Reviewed
- **Execution Type:** Manual
- **Suite:** 🔄 Regression, 🧪 Full
- **Requirements:**
  - N/A
- **Tags:** `registered`, `checkout`, `happy-path`
- **Author:** Antigravity (AI) (2026-04-17)
- **Reviewers:**
  - N/A

## Comments

> [!TIP]
> This test case requires an existing user account. The precondition assumes the account is fully set up with a default shipping address and a saved payment method. If not, the tester must create one first.

## Preconditions

- The Posters Demo Store is running.
- A registered user account exists with a default address and a saved payment method.
- The user is logged in.
- The browser cart is empty.

## Test Data

| Field | Value |
| :--- | :--- |
| Email | Registered user email |
| Password | Registered user password |

## Execution Targets

**Target Locales:**
- [x] EN-US
- [ ] EN-GB
- [x] DE-DE

**Target Viewports:**
- [x] Desktop (Large)
- [x] Mobile (Small)

---

## Steps

### 1. Add Item to Cart

- **Action:** Navigate to any product detail page and click "Add to Cart".
- **Data:** N/A
- **Verify:** The mini-cart updates to show 1 item.

### 2. Navigate to Checkout

- **Action:** Open the mini-cart or go to the cart page and click "Checkout".
- **Data:** N/A
- **Verify:** The user is taken directly to the Shipping Address step (bypassing the guest/login selection).

### 3. Verify Saved Shipping Address

- **Action:** Ensure the default saved shipping address is pre-selected and proceed.
- **Data:** N/A
- **Verify:** The Billing Address step or Payment step is displayed.

### 4. Verify Saved Billing Address

- **Action:** Ensure the default saved billing address is pre-selected and proceed.
- **Data:** N/A
- **Verify:** The Payment step is displayed.

### 5. Verify Saved Payment Method

- **Action:** Ensure the saved credit card is selected and proceed.
- **Data:** N/A
- **Verify:** The Order Review/Summary page is displayed.

### 6. Place Order

- **Action:** Review the totals and click "Place Order".
- **Data:** N/A
- **Verify:** The user is redirected to the Order Confirmation page, and an order number is displayed.

---

## Pass/Fail Criteria

- **Pass:** The registered user successfully checks out using their saved information without errors.
- **Fail:** The user is forced to re-enter saved information, or an error occurs during the flow.

---

## Postconditions

- The browser cart is empty.
- An order has been placed in the system and is visible in the user's Order History.

---

## Related Cases

- [TC_CHK_001: Guest Checkout (Happy Path)](./TC_CHK_001.md)
- [TC_CHK_003: Cart Modification During Checkout](./TC_CHK_003.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-04-17 | 1.0 | Antigravity (AI) | Initial creation |
