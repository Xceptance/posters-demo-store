# Registered Checkout Flow

## Metadata

- **Test ID:** TC_CHKT_002
- **Domain:** Checkout
- **Priority:** High
- **Status:** Draft
- **Execution Type:** Manual
- **Tags:** `checkout`, `registered-user`, `credit-card`, `happy-path`

### Execution Targets

**Target Locales:**
- [x] EN-US
- [x] EN-GB
- [x] DE-DE
- [x] SV-SE

**Target Viewports:**
- [x] Desktop (Large)
- [x] Mobile (Small)

## Description

This test verifies the checkout process for a user who already has an account, ensuring that their saved addresses and payment methods seamlessly populate the checkout process.

## Tester Notes

> [!TIP]
> **Keep an eye out for:**
> - Ensure registered addresses correctly pre-fill the dropdowns.
> - Verify the secure rendering of the saved credit card (masked except for the last 4 digits).

## Preconditions

- The Posters Demo Store is running.
- A user account exists with at least one saved Shipping Address, Billing Address, and validated Credit Card.
- The user is **logged in**.
- The browser cart is empty.

## Test Data

- **Login Credentials:** `johndoe@example.com` / `topsecret`

---

## Steps

| Step # | Action | Expected Result |
| :---: | :--- | :--- |
| 1 | Navigate to the storefront homepage. | The homepage loads successfully. The Cart shows `0` items. |
| 2 | Browse the catalog and click **"Add to Cart"** on any item. | A success notification is displayed. The cart item count increments to `1`. |
| 3 | Click the **Cart** icon and then click **"Checkout"**. | User is routed directly to the Shipping Address selection step instead of the Guest/Login page. |
| 4 | Select the saved Shipping Address from the dropdown and click **"Next"**. | The details are accepted and the Billing Address step is displayed. |
| 5 | Select **"Use Shipping Address"** (if same) or select the saved Billing Address. Click **"Next"**. | The details are accepted and the Payment Method step is displayed. |
| 6 | Select the saved Credit Card from the dropdown and click **"Next"**. | The Order Summary/Review page is displayed. |
| 7 | Review the order details, making sure the saved data reflects what is in the user's profile. Click **"Place Order"**. | The user is redirected to the Order Confirmation page. |
| 8 | Verify the Order Confirmation page. | An Order Number is displayed. |

---

## Postconditions

- The user's cart is empty.
- An order successfully recorded and associated with the registered customer account.
