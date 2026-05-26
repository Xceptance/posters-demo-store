# Cart — Price & Tax Totals Verification

A user adds a single product to the cart and verifies that the cart summary panel displays the correct subtotal, tax, shipping, and total values — with the tax calculated as `(subtotal + shipping) × taxRate`.

## Metadata

- **Test ID:** TC_CRT_001
- **Version:** 1.1
- **Software Version:** >= 1.0.0
- **Domains:** Cart
- **Priority:** 🔴 Critical
- **Status:** 👀 To Be Reviewed
- **Execution Type:** Manual
- **Suite:** 🚀 Smoke, 🔄 Regression, 🧪 Full
- **Requirements:**
  - BUS-BUG-23 (Tax calculation result wrong)
  - BUS-BUG-24 (Tax rate display format inconsistent)
  - BUS-BUG-25 (Tax displayed before shipping in summary)
- **Tags:** `cart`, `totals`, `tax`, `pricing`
- **Author:** Antigravity (AI) (2026-05-18)
- **Reviewers:**
  - N/A

## Comments

> [!WARNING]
> This test is expected to **FAIL** on the current build due to three known defects:
> - **BUS-BUG-23**: Tax amount is calculated incorrectly (shows $0.01 instead of ~$1.44 for a $24.00 base at 6%)
> - **BUS-BUG-24**: Tax rate format shows `6.0%` instead of required `6.00%`
> - **BUS-BUG-25**: Tax row is displayed *before* Shipping in the cart summary, which is incorrect given that tax is applied to `(subtotal + shipping)`

> [!TIP]
> Use a product with a simple, predictable price (e.g., $17.00). Record the actual subtotal and shipping amounts shown before asserting the tax math.

## Preconditions

- The Posters Demo Store is running.
- The user is not logged in.
- The browser cart is empty.

## Test Data

| Field | Value |
| :--- | :--- |
| Tax Rate | `6.00%` |
| Shipping Cost | `$7.00` (default flat rate) |

## Execution Targets

**Target Locales:**
- [x] EN-US
- [ ] EN-GB
- [x] DE-DE
- [ ] SV-SE
- [ ] JA-JP

**Target Viewports:**
- [x] Desktop (Large)
- [x] Mobile (Small)

---

## Steps

### 1. Add a Product to Cart

- **Action:** Navigate to any product detail page, select a size and finish, and click "Add to Cart".
- **Data:** N/A
- **Verify:** The mini-cart icon updates to show 1 item.

### 2. Open the Cart Page

- **Action:** Open the mini-cart and click "View Cart" (or navigate directly to `/cart`).
- **Data:** N/A
- **Verify:** The cart page loads and shows the added product as a line item with a unit price.

### 3. Verify Line Item Price

- **Action:** Note the product unit price shown in the line item (`#total-<lineItemId>`).
- **Data:** N/A
- **Verify:** The line item price matches the product's displayed price from the PDP.

### 4. Verify Subtotal (`#cart-subtotal`)

- **Action:** Read the **Subtotal** value from the cart summary panel.
- **Data:** N/A
- **Verify:** Subtotal equals the sum of all line item prices. For a single item this is the unit price × quantity.

### 5. Verify Tax Rate Display

- **Action:** Read the tax rate label in the cart summary (shown as part of the "Tax" row label).
- **Data:** N/A
- **Verify:** The tax rate is displayed as **`6.00%`** (two decimal places). *(Expected to FAIL — BUS-BUG-24: currently shows `6.0%`)*

### 6. Verify Tax Row Order

- **Action:** Inspect the visual order of the cart summary rows.
- **Data:** N/A
- **Verify:** The rows appear in this sequence: **Subtotal → Shipping → Tax → Total**. *(Expected to FAIL — BUS-BUG-25: current order is `Subtotal → Tax → Shipping → Total`)*

### 7. Verify Tax Amount (`#cart-tax`)

- **Action:** Read the **Tax** amount (`#cart-tax`) from the cart summary.
- **Data:** Tax Rate: `6%`, Subtotal from Step 4, Shipping: `$7.00`
- **Verify:** Tax amount = `(subtotal + shipping) × 0.06`. Example: subtotal $17.00 + shipping $7.00 = $24.00 × 6% = **$1.44**. *(Expected to FAIL — BUS-BUG-23: currently shows $0.01)*

### 8. Verify Shipping Cost

- **Action:** Read the **Shipping** value from the cart summary.
- **Data:** N/A
- **Verify:** Shipping cost is displayed and equals **$7.00**.

### 9. Verify Total (`#cart-total`)

- **Action:** Read the **Total** value (`#cart-total`) from the cart summary.
- **Data:** Subtotal from Step 4, Shipping $7.00, Tax from Step 7
- **Verify:** Total = subtotal + shipping + tax. Example: $17.00 + $7.00 + $1.44 = **$25.44**.

---

## Pass/Fail Criteria

- **Pass:** All summary values are mathematically correct: tax = `(subtotal + shipping) × taxRate`, total = `subtotal + shipping + tax`, tax rate shown as `6.00%`, and rows appear in `Subtotal → Shipping → Tax → Total` order.
- **Fail:** Any of the following:
  - Tax amount does not equal `(subtotal + shipping) × taxRate` (within ±$0.02)
  - Total does not equal `subtotal + shipping + tax` (within ±$0.02)
  - Tax rate label is not `6.00%`
  - Tax row appears before Shipping row in the summary

---

## Postconditions

- Cart remains populated (no cleanup required).

---

## Related Cases

- [TC_CHK_001: Guest Checkout (Happy Path)](../checkout/TC_CHK_001.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-05-18 | 1.0 | Antigravity (AI) | Initial creation — covers BUS-BUG-23, BUS-BUG-24, BUS-BUG-25 |
| 2026-05-25 | 1.1 | Gemini (AI) | Added JA-JP locale to Target Locales list |
