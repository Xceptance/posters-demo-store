# Database Schema Analysis

The database for the posters-demo-store is clearly structured around a typical e-commerce platform. It consists of 18 tables handling users, products, categories, carts, orders, and localization. 

Here is the complete breakdown and an assessment of the database design's "soundness".

---

## 🏗️ Schema Overview

### 1. User & Identity
*   **`customer`**: Stores customer accounts. Uses `UUID` for keys. Stores email, hashed password, and names. It has a one-to-one relationship with their active `cart`.
*   **`backofficeuser`**: Separate table for admin users. Also uses `UUID`.

### 2. Shopping Cart & Checkout
*   **`cart`**: Represents a user's active shopping session. Links to a customer, selected addresses, and credit card. Tracks subtotal, shipping, taxes, and total price.
*   **`cartproduct`**: The items inside a cart. Links to a `product` and a `cart`, tracking quantity, selected finish, poster size, and the price at the time of adding.
*   **`creditcard`**: Stores payment info (card number, name, expiry).
*   **`billingaddress` / `shippingaddress`**: Two identical tables storing address details (name, company, street, city, state, country, zip).

### 3. Order Management
*   **`ordering`**: Created upon checkout. Captures the finalized order state (snapshots of address IDs, costs, taxes, and the customer ID). Uses `UUID`.
*   **`orderproduct`**: The items within a finalized order, snapshotting the price, count, and finish.

### 4. Product Catalog
*   **`product`**: The core product listing. Contains image URLs, flags for visibility (e.g., `show_in_carousel`), and references to translations (`name_id`, `description_detail_id`) and categories.
*   **`topcategory` / `subcategory`**: Two-tier category hierarchy. Subcategories map to a top category.
*   **`postersize` / `productpostersize`**: `postersize` defines available dimensions (width/height), while `productpostersize` acts as a mapping table to set prices for specific products at specific sizes.

### 5. Localization & i18n
*   **`defaulttext`**: Stores base strings in a generic format.
*   **`supportedlanguage`**: Lists available languages (code, endonym, fallback).
*   **`translation`**: Maps a `defaulttext` ID to a specific language ID with the translated text.

---

## 🚨 Soundness Check & Assessment

Overall, the schema successfully models an e-commerce platform, but there are several significant flaws and anti-patterns. If this were a real production application, these would need to be addressed. Since this is a demo store, many of these are likely intentional simplifications.

### 🔴 Critical Issues

1.  **Using `DOUBLE` for Monetary Values:**
    *   **Issue:** All prices and costs (`price`, `total_price`, `shipping_costs`, `tax`, etc.) are stored as `DOUBLE` (floating-point). 
    *   **Why it's bad:** Floating-point numbers cannot accurately represent base-10 decimals, leading to rounding errors in financial calculations (e.g., `$0.10 + $0.20 = $0.30000000000000004`).
    *   **Fix:** Use `DECIMAL(10,2)` or `NUMERIC`, or store values as `INTEGER` representing the smallest currency unit (e.g., cents).

2.  **Date Stored as a String:**
    *   **Issue:** `ordering.order_date` is a `VARCHAR(255)`.
    *   **Why it's bad:** You cannot perform standard date operations natively in SQL (e.g., sorting chronologically, filtering by date ranges like "orders in the last 30 days").
    *   **Fix:** Use a native `TIMESTAMP` or `DATE` column. *(Note: `cartproduct.last_update` correctly uses `TIMESTAMP`)*.

3.  **Storing Raw Credit Card Numbers:**
    *   **Issue:** `creditcard.card_number` is stored as a `VARCHAR(255)`.
    *   **Why it's bad:** Storing raw credit card numbers is a massive PCI-DSS compliance violation and an extreme security risk.
    *   **Fix:** E-commerce platforms should never store PANs (Primary Account Numbers). They should use a payment gateway (like Stripe or Braintree) and only store the gateway's opaque token or the last 4 digits.

### 🟡 Structural / Design Flaws

4.  **Denormalization in the Product Table:**
    *   **Issue:** The `product` table has both `subcategory_id` and `top_category_id`. 
    *   **Why it's bad:** Since a `subcategory` already strictly belongs to a `topcategory`, storing `top_category_id` directly on the `product` is redundant and introduces the risk of data inconsistency (e.g., a product assigned to Subcategory A but mistakenly linked to Top Category B).

5.  **Address Table Duplication:**
    *   **Issue:** `billingaddress` and `shippingaddress` are two completely identical tables.
    *   **Why it's bad:** It requires maintaining two identical schemas.
    *   **Fix:** Consolidate into a single `address` table. You can link them directly to the `cart`/`ordering` tables as `shipping_address_id` and `billing_address_id`. 

6.  **Bidirectional Circular Relationship (Cart <-> Customer):**
    *   **Issue:** `customer` has a `cart_id` with a unique constraint, and `cart` has a `customer_id` with a unique constraint.
    *   **Why it's bad:** Circular relationships make inserting and deleting records complex due to chicken-and-egg constraint checks.
    *   **Fix:** Usually, the `cart` belongs to a `customer`, so `cart` should hold the `customer_id` (or vice-versa). 

7.  **Missing Foreign Key Constraints:**
    *   **Issue:** Many relation columns lack actual SQL Foreign Keys at the bottom of the schema.
    *   *Examples:* `cart.shipping_address_id`, `cart.billing_address_id`, `cart.credit_card_id`, `ordering.customer_id`, `defaulttext.original_language_id`.
    *   **Why it's bad:** Without hard foreign constraints, the database cannot prevent orphaned records if a parent is deleted.

### 🟢 Good Practices Spotted

*   **Mixed ID Strategy:** Using `UUID`s for globally discoverable items (Carts, Orders, Users) prevents enumeration attacks (e.g., guessing Order #105 to view someone else's receipt). Standard Integer IDs are suitably used for internal catalog state (Products, Categories).
*   **Snapshotting Order Details:** The `ordering` and `orderproduct` tables correctly copy and freeze prices and details at the time of checkout. This ensures that if a product's price changes *later*, historical order receipts aren't modified.
