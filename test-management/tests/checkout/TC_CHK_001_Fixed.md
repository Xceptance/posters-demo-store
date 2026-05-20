# Guest Checkout with Fixed Product (Happy Path)

A new guest user adds the fixed product **"Grizzly Bear"** (size `16x12`, finish `Matte`) to the cart, proceeds to checkout, enters shipping and billing details, provides credit card payment, reviews the order with exact expected financial calculations, and successfully places it.

## Metadata

- **Test ID:** TC_CHK_001_Fixed
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Checkout, Cart
- **Priority:** 🔴 Critical
- **Status:** 👀 To Be Reviewed
- **Execution Type:** Manual
- **Suite:** 🚀 Smoke, 🔄 Regression, 🧪 Full
- **Requirements:**
  - N/A
- **Tags:** `guest`, `checkout`, `happy-path`, `fixed-product`
- **Author:** Antigravity (AI) (2026-05-20)
- **Reviewers:**
  - N/A

## Comments

> [!TIP]
> This test case uses the **"Grizzly Bear"** product (variant: `P001-GRIBEA-V1M`, size `16x12`, finish `Matte`) to provide completely predictable prices and calculations, making validation extremely direct and reliable.

## Preconditions

- The Posters Demo Store is running.
- The user is not logged in.
- The browser cart is empty.

## Test Data

### Customer Details
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

### Product Selection
- **Product Name:** `Grizzly Bear` (DE: `Grizzlybär`, SE: `Grizzlybjörn`, JP: `ハイイログマ`)
- **Size:** `16x12`
- **Finish:** `Matte` (JP: `マット`)

### Expected Financial Totals by Locale
| Locale | Base Subtotal | Shipping | Tax Rate | Expected Tax | Expected Total |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **en-US (USD)** | `$17.00` | `$7.00` | `6.00%` | `$1.44` | `$25.44` |
| **en-GB (GBP)** | `£13.43` | `£7.00` | `6.00%` | `£1.23` | `£21.66` |
| **de-DE (EUR)** | `14,96 €` | `7,00 €` | `6,00%` | `1,32 €` | `23,28 €` |
| **sv-SE (SEK)** | `185,30 kr` | `7,00 kr` | `6,00%` | `11,54 kr` | `203,84 kr` |
| **ja-JP (JPY)** | `￥17` | `￥7` | `6.00%` | `￥1` *(rounded JPY)* | `￥25` *(rounded JPY)* |

---

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

### 1. Search and Select the Product
- **Action:** Open the home page, search for the product name (`Grizzly Bear` or localized equivalent) in the search bar. Click the product to open its detail page.
- **Verify:** The product detail page is displayed.

### 2. Add Fixed Product to Cart
- **Action:** Select size **`16x12`** and finish **`Matte`** (JP: `マット`), then click "Add to Cart".
- **Verify:** The mini-cart icon updates to show 1 item.

### 3. Navigate to Checkout
- **Action:** Open the cart page and click "Checkout".
- **Verify:** The user is redirected to the Shipping Address page (`/checkout/shippingAddress`).
- **Verify (tax rate & subtotal):** The cart summary displays the exact expected subtotal for the active locale, and the tax rate is displayed in the exact format **`6.00%`** (or `6,00 %`).

### 4. Enter Shipping Details
- **Action:** Fill in the shipping address form fields and click "Continue to Billing".
- **Verify:** The user is redirected to the Billing Address page.

### 5. Copy Shipping Address to Billing
- **Action:** Check the "Same as shipping address" checkbox at the top of the billing form.
- **Verify:** All billing address fields are populated. Click "Continue to Payment".

### 6. Enter Payment Details
- **Action:** Enter the credit card details in the payment form and click "Continue to Review".
- **Verify:** The card vendor badge (Visa) is displayed. The user is redirected to the Review & Place Order page.

### 7. Review Order and Verify Exact Totals
- **Action:** Review all displayed information in the order summary panel.
- **Verify:**
  - The order items table lists **`Grizzly Bear`** with size `16x12`, finish `Matte`, quantity `1`.
  - The Subtotal, Shipping, Tax, and Total match **exactly** the expected values for the active locale as listed in the Test Data section.
  - The tax rate label displays exactly **`6.00%`** (or `6,00 %`).
  - A "Place Order" button (`#btn-place-order`) is visible.

### 8. Place Order
- **Action:** Click "Place Order".
- **Verify:**
  - The user is redirected to the Order Confirmation page.
  - A success banner and a non-empty **Order ID** (format: `ORD-[0-9]{10,15}`) are shown.
  - The order summary panel displays the correct Subtotal, Tax, Shipping, and Total paid, matching **exactly** the target values for the active locale.

---

## Pass/Fail Criteria

- **Pass:** The guest user successfully completes all checkout steps using the fixed product, and the calculated/displayed totals (Subtotal, Tax, Shipping, Total) match the expected table values **exactly** (including whole JPY rounding).
- **Fail:** Any of the following:
  - The user is blocked or receives a server error.
  - The displayed pricing totals do not match the expected locale values.
  - The JPY tax is not rounded to `￥1` or JPY total is not rounded to `￥25`.
  - The Order ID is missing or empty.
