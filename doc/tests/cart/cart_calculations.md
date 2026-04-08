# Cart Updates and Total Calculations

## Metadata

- **Test ID:** TC_CRT_001
- **Domain:** Cart
- **Priority:** Critical
- **Status:** Draft
- **Execution Type:** Manual
- **Tags:** `cart`, `pricing`, `tax`, `calculations`

### Execution Targets

**Target Locales:**
- [x] EN-US
- [x] SV-SE

**Target Viewports:**
- [x] Desktop (Large)

## Description

This test asserts that when items are added to the cart, the quantity incrementation works correctly and immediately updates both the subtotal and total calculations (inclusive of tax rules dynamically).

## Tester Notes

> [!CAUTION]
> **Currency Verification:**
> Pay close attention to the currency symbol position and decimal formatting, as these change strictly between EN-US ($10.50) and SV-SE (10,50 kr).

## Preconditions

- The Posters Demo Store is running.
- The browser cart is empty.

## Test Data

- **Item 1:** Any poster with a base price of `$10.00` (or `100 kr`).
- **Item 2:** Any poster with a base price of `$20.00` (or `200 kr`).

---

## Steps

| Step # | Action | Expected Result |
| :---: | :--- | :--- |
| 1 | Navigate to the storefront homepage. | The homepage loads successfully. |
| 2 | Go to a product detail page (Item 1) and click **"Add to Cart"**. | Header cart icon reads `1`. |
| 3 | Navigate to the Cart page. | Cart lists Item 1, Qty 1. Subtotal = Item Price. |
| 4 | Change the quantity input of Item 1 to `3` and click **"Update"**. | Subtotal updates to `3 * Price`. Tax and Shipping are recalculated properly in the Order Summary box. |
| 5 | Go to a different product detail page (Item 2) and click **"Add to Cart"**. | Header cart icon reads `4`. |
| 6 | Return to the Cart page. | Cart lists both Item 1 (Qty 3) and Item 2 (Qty 1). |
| 7 | Verify the Total Calculation. | Total correctly sums (Item 1 Subtotal + Item 2 Subtotal) + Shipping + Tax. |
| 8 | Click the **Remove** / **X** button next to Item 1. | Item 1 is removed from the cart. Header reads `1`. |
| 9 | Verify the Recalculation. | Total is now strictly based on Item 2 + recalculated tax. |

---

## Postconditions

- The cart only contains Item 2.
