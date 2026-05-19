# Guest Checkout (Happy Path)

A new guest user adds an item to the cart, proceeds to checkout, enters shipping and billing details, provides credit card payment, reviews the order, and successfully places it.

## Execution Result

| Who | When | Result | Where | Comment |
| :--- | :--- | :--- | :--- | :--- |
| rschwietzke | 2026-05-18 | `❌ FAILED` | Desktop / EN-US / Local | Steps 1–6 passed. Tax calculation wrong on confirmation: $0.01 at 6% on $17.00 (expected $1.02). Filed BUS-BUG-23. |

## Metadata

- **Test ID:** TC_CHK_001
- **Version:** 1.1
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
| Card Number | `1111222233334444` |
| Name on Card | `John Doe` |
| Expiry (MM/YY) | `12/30` |
| CVV | `123` |

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

- [x] **Action:** Navigate to any product detail page, select a size and finish option, and click "Add to Cart".
- **Data:** N/A
- **Verify:** The mini-cart icon updates to show 1 item.

### 2. Navigate to Checkout

- [x] **Action:** Open the cart page and click "Checkout".
- **Data:** N/A
- **Verify:** The user is redirected to the Shipping Address page (`/checkout/shippingAddress`). The checkout progress indicator shows Step 1 of 5 active.

### 3. Enter Shipping Details

- [x] **Action:** Fill in the shipping address form fields and click "Continue to Billing".
- **Data:** `First Name`, `Last Name`, `Company`, `Address`, `City`, `State`, `Zip`, `Country`.
- **Verify:** The user is redirected to the Billing Address page. The progress indicator advances to Step 2.

### 4. Copy Shipping Address to Billing

- [x] **Action:** Check the "Same as shipping address" checkbox at the top of the billing form.
- **Data:** N/A
- **Verify:** All billing address fields are automatically populated with the shipping address data. Click "Continue to Payment".

### 5. Enter Payment Details

- [x] **Action:** Enter the credit card details in the payment form and click "Continue to Review". ✅ PASSED with corrected card number `4111111111111111` (Visa). Original test data `1111222233334444` showed "Unknown" — expected, as it has no valid BIN prefix.
- **Data:** `Card Number`, `Name on Card`, `Expiry (MM/YY)`, `CVV`.
- **Verify:** The card vendor badge is detected and displayed (e.g., Visa / Mastercard). The user is redirected to the Review & Place Order page (Step 4). The masked card number and vendor are shown in the order summary panel.

### 6. Review Order

- [x] **Action:** Review all displayed information without clicking anything.
- **Data:** N/A
- **Verify:**
  - The order items table lists the correct product, size, finish, quantity, and price.
  - The summary panel shows a non-zero Subtotal, Tax, Shipping, and Total.
  - The masked card number and card vendor are displayed in the Payment Method section.
  - A "Place Order" button (`#btn-place-order`) is visible.

### 7. Place Order

- [x] **Action:** Click "Place Order". ⚠️ PARTIAL — Confirmation page loaded, Order ID `ORD-1779135652864` displayed (ORD-format by design, not UUID — test case corrected). "Order Confirmed!" banner shown. Addresses correct. **BUG: Tax $0.01 on $17.00 at 6% — expected $1.02. Filed as BUS-BUG-23.**
- **Data:** N/A
- **Verify:** The user is redirected to the Order Confirmation page (Step 5). An "Order Confirmed!" success banner is displayed. A non-empty Order ID (UUID) is displayed. The order summary shows Subtotal, Tax, Shipping, and Total paid. The shipping and billing addresses match the entered data.

---

## Pass/Fail Criteria

- **Pass:** The guest user navigates all 5 checkout steps (Shipping → Billing → Payment → Review → Confirmation) without errors, and the Order Confirmation page displays a non-empty Order ID.
- **Fail:** Any of the following:
  - The user is blocked or receives a server error (4xx/5xx) at any step.
  - The cart is empty upon reaching the Review page.
  - The Order ID is missing or empty on the Confirmation page.
  - The financial totals (Subtotal, Tax, Shipping, Total) are zero or inconsistent.
  - The "Same as shipping" checkbox does not populate billing fields.

---

## Postconditions

- The browser cart is empty.
- An order has been placed in the system.

---

## Related Cases

- [TC_CHK_002: Registered User Checkout](../../tests/checkout/TC_CHK_002.md)
- [TC_CHK_003: Cart Modification During Checkout](../../tests/checkout/TC_CHK_003.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-04-17 | 1.0 | Antigravity (AI) | Initial creation |
| 2026-05-18 | 1.1 | Antigravity (Claude Sonnet 4.6) | Corrected against actual templates: unified MM/YY expiry + added CVV + Name on Card; removed non-existent shipping method step; fixed Step 2 verify (no guest/registered interstitial); made billing step precise; expanded Step 6 review verifications; expanded Pass/Fail criteria. |
