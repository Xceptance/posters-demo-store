# Guest Checkout (Happy Path)

## Execution Result

| Who | When | Result | Where | Comment |
| :--- | :--- | :--- | :--- | :--- |
| AI Copilot | 2026-05-06 | `✅ PASSED` | Local Environment | Noted usability bug with CC autofill overflowing (logged as BUS-BUG-21). |

A new guest user adds an item to the cart, proceeds to checkout, enters shipping/billing details, selects a shipping method, provides payment, and successfully places the order.

## Metadata

- **Test ID:** TC_CHK_001
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Checkout, Cart
- **Priority:** 🔴 Critical
- **Status:** 👀 To Be Reviewed
- **Execution Type:** Manual
- **Suite:** 🚀 Smoke, 🔄 Regression, 🧪 Full
- **Requirements:**
  - N/A
- **Tags:** `guest`, `checkout`, `happy-path`
- **Author:** Antigravity (AI) (2026-04-17)
- **Reviewers:**
  - N/A

## Comments

> [!TIP]
> Ensure the browser cart and session are entirely clean before starting this test to accurately simulate a new guest user.

## Preconditions

- The Posters Demo Store is running.
- The user is not logged in.
- The browser cart is empty.

## Test Data

| Field | Value |
| :--- | :--- |
| First Name | `John` |
| Last Name | `Doe` |
| Company | `Acme Corp` |
| Address | `123 Main St` |
| City | `Austin` |
| State | `Texas` |
| Zip | `78701` |
| Country | `United States` |
| Email | `john.doe@example.com` |
| Credit Card | `1111222233334444` |
| Exp Month | `12` |
| Exp Year | `2030` |

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

### [x] 1. Add Item to Cart

- **Action:** Navigate to any product detail page, select an option (if applicable), and click "Add to Cart".
- **Data:** N/A
- **Verify:** The mini-cart updates to show 1 item.

### [x] 2. Navigate to Checkout

- **Action:** Open the mini-cart or go to the cart page and click "Checkout".
- **Data:** N/A
- **Verify:** The user is redirected to the Guest/Registered Login selection page.

### [x] 3. Proceed as Guest

- **Action:** Select "Checkout as Guest" (or equivalent option).
- **Data:** N/A
- **Verify:** The user is presented with the Shipping Address form.

### [x] 4. Enter Shipping Details

- **Action:** Fill out the shipping address form and proceed.
- **Data:** Use all `Test Data` values for address and contact info.
- **Verify:** The billing address form or payment step is displayed. The entered shipping details are saved to the session.

### [x] 5. Confirm Billing Details

- **Action:** Select "Use shipping address for billing" (if available) or re-enter the same details. Proceed.
- **Data:** N/A
- **Verify:** The Payment method or Order Review step is displayed.

### [x] 6. Enter Payment Details

- **Action:** Select credit card as the payment method, enter the card details, and proceed.
- **Data:** `Credit Card`, `Exp Month`, `Exp Year`.
- **Verify:** The Order Review/Summary page is displayed.

### [x] 7. Place Order

- **Action:** Review the totals and click "Place Order".
- **Data:** N/A
- **Verify:** The user is redirected to the Order Confirmation page, and an order number is displayed.

---

## Pass/Fail Criteria

- **Pass:** The guest user successfully navigates all checkout steps and receives an order confirmation number without errors.
- **Fail:** The user is blocked at any step, the cart drops items, or a 500 server error occurs.

---

## Postconditions

- The browser cart is empty.
- An order has been placed in the system.

---

## Related Cases

- [TC_CHK_002: Registered User Checkout](./TC_CHK_002.md)
- [TC_CHK_003: Cart Modification During Checkout](./TC_CHK_003.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-04-17 | 1.0 | Antigravity (AI) | Initial creation |
