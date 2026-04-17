# Cart Modification During Checkout

A user starts the checkout process, navigates back to the cart to change item quantities, and resumes checkout to ensure the order totals are correctly recalculated.

## Metadata

- **Test ID:** TC_CHK_003
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Checkout, Cart
- **Priority:** 🟡 Medium
- **Status:** 👀 To Be Reviewed
- **Execution Type:** Manual
- **Suite:** 🔄 Regression, 🧪 Full
- **Requirements:**
  - N/A
- **Tags:** `cart`, `checkout`, `modification`
- **Author:** Antigravity (AI) (2026-04-17)
- **Reviewers:**
  - N/A

## Comments

> [!CAUTION]
> Watch out for the order total recalculation. Sometimes navigating back from the payment step can cause stale cache issues on the cart page.

## Preconditions

- The Posters Demo Store is running.
- The user is logged in or proceeds as a guest.
- The browser cart is empty.

## Test Data

| Field | Value |
| :--- | :--- |
| First Name | `John` |
| Last Name | `Doe` |
| Email | `john.doe@example.com` |

## Execution Targets

**Target Locales:**
- [x] EN-US
- [ ] EN-GB
- [ ] DE-DE

**Target Viewports:**
- [x] Desktop (Large)
- [ ] Mobile (Small)

---

## Steps

### 1. Add Item to Cart

- **Action:** Navigate to a product detail page and add 1 item to the cart. Note the item price.
- **Data:** N/A
- **Verify:** The mini-cart updates to show 1 item.

### 2. Proceed to Checkout

- **Action:** Go to the cart page, verify the subtotal, and click "Checkout".
- **Data:** N/A
- **Verify:** The user is on the first step of checkout (Shipping Address).

### 3. Enter Partial Details

- **Action:** Enter some shipping information.
- **Data:** `First Name`, `Last Name`.
- **Verify:** Information is entered.

### 4. Navigate Back to Cart

- **Action:** Click the "Cart" link or back button to return to the shopping cart.
- **Data:** N/A
- **Verify:** The cart page is displayed, showing the original 1 item and subtotal.

### 5. Modify Cart Quantity

- **Action:** Change the quantity of the item from 1 to 2, and update the cart.
- **Data:** Quantity = `2`
- **Verify:** The cart subtotal updates to reflect the new quantity.

### 6. Resume Checkout

- **Action:** Click "Checkout" again.
- **Data:** N/A
- **Verify:** The user is returned to the checkout flow.

### 7. Complete Order

- **Action:** Complete the remaining checkout steps and proceed to the Order Review page.
- **Data:** N/A
- **Verify:** The Order Review page accurately reflects the new quantity (2) and the recalculated total order cost.

---

## Pass/Fail Criteria

- **Pass:** The user can seamlessly transition between checkout and cart, and the final order total accurately reflects the modified cart quantity.
- **Fail:** The cart quantity reverts, the total is incorrect, or the checkout process errors out upon return.

---

## Postconditions

- The browser cart is empty (if the order is placed).

---

## Related Cases

- [TC_CHK_001: Guest Checkout (Happy Path)](./TC_CHK_001.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-04-17 | 1.0 | Antigravity (AI) | Initial creation |
