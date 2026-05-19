# Guest Checkout (Happy Path)

## Execution Result

| Who | When | Result | Where | Comment |
| :--- | :--- | :--- | :--- | :--- |
| AI | 2026-05-19 | `✅ PASSED` | Desktop/Chrome | |

A new guest user adds an item to the cart, proceeds to checkout, enters shipping and billing details, provides credit card payment, reviews the order, and successfully places it.

## Metadata

- **Test ID:** TC_CHK_001
- **Version:** 1.4
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
| Card Number | `4111111111111111` |
| Name on Card | `John Doe` |
| Expiry (MM/YY) | `12/30` |
| CVV | `123` |

## Execution Targets

**Target Locales:**
- [x] EN-US
- [x] EN-GB
- [x] DE-DE
- [x] SV-SE
- [x] JA-JP

**Target Viewports:**
- [x] Desktop (Large)
- [x] Mobile (Small)

---

## Steps

### 1. Add Item to Cart

- **Action:** Navigate to any product detail page, select a size and finish option, and click "Add to Cart".
- **Data:** N/A
- **Verify:** The mini-cart icon updates to show 1 item.

### 2. Navigate to Checkout

- **Action:** Open the cart page and click "Checkout".
- **Data:** N/A
- **Verify:** The user is redirected to the Shipping Address page (`/checkout/shippingAddress`). The checkout progress indicator shows Step 1 of 5 active.
- **Verify (tax rate):** The tax rate in the cart summary is displayed with exactly two decimal places (e.g., **`6.00%`** or localized equivalent **`6,00 %`**). `6.0%` or `6%` are not acceptable formats. *(BUS-BUG-24)*

### 3. Enter Shipping Details

- **Action:** Fill in the shipping address form fields and click "Continue to Billing".
- **Data:** `First Name`, `Last Name`, `Company`, `Address`, `City`, `State`, `Zip`, `Country`.
- **Verify:** The user is redirected to the Billing Address page. The progress indicator advances to Step 2.

### 4. Copy Shipping Address to Billing

- **Action:** Check the "Same as shipping address" checkbox at the top of the billing form.
- **Data:** N/A
- **Verify:** All billing address fields are automatically populated with the shipping address data. Click "Continue to Payment".

### 5. Enter Payment Details

- **Action:** Enter the credit card details in the payment form and click "Continue to Review".
- **Data:** `Card Number`, `Name on Card`, `Expiry (MM/YY)`, `CVV`.
- **Verify:** The card vendor badge is detected and displayed (e.g., Visa / Mastercard). The user is redirected to the Review & Place Order page (Step 4). The masked card number and vendor are shown in the order summary panel.

### 6. Review Order

- **Action:** Review all displayed information without clicking anything.
- **Data:** N/A
- **Verify:**
  - The order items table lists the correct product, size, finish, quantity, and price.
  - The summary panel shows a non-zero Subtotal, Tax, Shipping, and Total.
  - The tax rate label reads the localized equivalent of **`6.00%`** (e.g., `6,00 %`). It must have two decimal places. `6.0%` is not acceptable. *(BUS-BUG-24)*
  - The calculated tax is exactly `(Subtotal + Shipping) * 6.00%`. *(BUS-BUG-23)*
  - The total sum is exactly `Subtotal + Shipping + Tax`.
  - The masked card number and card vendor are displayed in the Payment Method section.
  - A "Place Order" button (`#btn-place-order`) is visible.

### 7. Place Order

- **Action:** Click "Place Order".
- **Data:** N/A
- **Verify:** The user is redirected to the Order Confirmation page (Step 5). An "Order Confirmed!" success banner is displayed. A non-empty **Order ID** (format: `ORD-XXXXXXXXXXXX`) is displayed. The order summary shows Subtotal, Tax, Shipping, and Total paid. The tax rate label reads the localized equivalent of **`6.00%`** (two decimal places) *(BUS-BUG-24)*. Verify the calculated tax is `(Subtotal + Shipping) * 6.00%` and the Total paid equals `Subtotal + Shipping + Tax`. The shipping and billing addresses match the entered data.

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

- [TC_CHK_002: Registered User Checkout](./TC_CHK_002.md)
- [TC_CHK_003: Cart Modification During Checkout](./TC_CHK_003.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-04-17 | 1.0 | Antigravity (AI) | Initial creation |
| 2026-05-18 | 1.1 | Antigravity (Claude Sonnet 4.6) | Corrected against actual templates: unified MM/YY expiry + added CVV + Name on Card; removed non-existent shipping method step; fixed Step 2 verify (no guest/registered interstitial); made billing step precise; expanded Step 6 review verifications; expanded Pass/Fail criteria. |
| 2026-05-18 | 1.2 | Antigravity (Claude Sonnet 4.6) | Fixed test data: card number corrected from invalid `1111222233334444` to standard Visa test number `4111111111111111`. Discovered during test execution — original number had no valid BIN prefix, resulting in "Unknown" vendor detection. |
| 2026-05-18 | 1.3 | Antigravity (Claude Sonnet 4.6) | Fixed Order ID description in Step 7 and Pass/Fail: Order ID is `ORD-XXXXXXXXXXXX` format by design, not UUID. |
| 2026-05-18 | 1.4 | Antigravity (Claude Sonnet 4.6) | Added tax rate format verification (`6.00%`) to Steps 2, 6, and 7 for cart, review, and confirmation pages respectively. References BUS-BUG-24. |
| 2026-05-19 | 1.5 | Antigravity (AI) | Added explicit verification for sum and tax calculations (`(Subtotal + Shipping) * 6.00%`) in Steps 6 and 7. References BUS-BUG-23. |
