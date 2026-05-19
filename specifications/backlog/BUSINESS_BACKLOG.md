# Business Backlog

> [!IMPORTANT]
> This document tracks all business-focused Epics, Features, Improvements, and Tasks.
> **Numbering Policy**: Never reuse ID numbers. When adding new items, always count upwards from the highest existing number, even if earlier items are deleted or moved.

## Epics
- [ ] BUS-EPIC-1: Backoffice
- [ ] BUS-EPIC-2: Coupons
- [ ] BUS-EPIC-3: Inventory
- [ ] BUS-EPIC-4: PCI Compliance Guidelines — Establish strict guidelines for PCI handling across all areas (storefront and backoffice) to ensure credit card numbers are never exposed in server memory or stored unencrypted.

## Features
- [x] BUS-FEAT-1: Footer checkout
- [ ] BUS-FEAT-2: Hero header image category
- [ ] BUS-FEAT-3: Steps Checkout
- [ ] BUS-FEAT-4: Sorting
- [ ] BUS-FEAT-5: Refinements for search — Currently, all user input is sanitized via `MultiFieldQueryParser.escape()` before Lucene parsing, which neutralizes query syntax characters (`-`, `"`, `+`, `:`, etc.). This means advanced search features like negation (`-term`), exact phrase matching (`"exact phrase"`), and boolean operators are **not available** to end users. Consider selectively allowing safe Lucene syntax (e.g., quoted phrases for exact matching, `-` for exclusion) while still protecting against injection.
- [ ] BUS-FEAT-6: Markdown styling of product and other texts
- [ ] BUS-FEAT-7: SKU search — Product SKU codes (e.g., `P001-GRIBEA-V1M`) are not included in the Lucene search index. Adding SKU as a searchable field would enable support/backoffice lookup scenarios directly from the storefront search bar.
- [ ] BUS-FEAT-8: Password requirements — No password validation exists for storefront registration or admin user creation. Passwords of any length/complexity are accepted and hashed. Add basic requirements (e.g., minimum length, complexity rules) for both storefront customers and backoffice admin users.
- [ ] BUS-FEAT-9: Storefront password change — The storefront does not currently offer a password-change flow for logged-in customers. Adding this would allow `CustomerProfile.lastPasswordChange` to be updated at runtime.
- [x] ~~BUS-FEAT-10: Backoffice password reset~~ — **INVALIDATED**: Not needed. Customers can reset their own passwords via the storefront (`BUS-FEAT-9`). Admin-triggered resets are unnecessary overhead.
- [ ] BUS-FEAT-11: Shipping Methods — Allow administrators to define multiple named shipping options (e.g., Standard, Express, Overnight) with configurable costs and estimated delivery windows. The storefront checkout flow must present these options between Shipping Address and Payment so the customer can select a method; the selected method's cost must be reflected in the order totals. The backoffice (Checkout / Shipping module) must provide CRUD management of shipping methods (name, description, cost, estimated delivery days, enabled/disabled toggle). This feature touches:
  - **Storefront checkout**: new "Select Shipping Method" step (Step 2 of 5, between Shipping Address and Billing, or as a distinct step), persisted to session/order.
  - **Backoffice — Checkout module**: new `Shipping Methods` submodule under a top-level `Checkout` module for administrators to manage available shipping options.
  - **Order model**: `Order` and `Cart` entities must store the selected shipping method name and cost.
  - **Localization**: method names and descriptions should support the existing locale system.

## Improvements
- [ ] BUS-IMPR-1: Localization of messages such as "this is a demo store", also the homepage message
- [ ] BUS-IMPR-2: Localization of ad banner
- [ ] BUS-IMPR-3: Styling pagination including colors
- [ ] BUS-IMPR-4: Show rest count when refining
- [ ] BUS-IMPR-5: Double password when setting account
- [x] BUS-IMPR-6: Cart should not close when mouse hovers over it and stay open longer
- [ ] BUS-IMPR-7: Close X for enlarged images needs contrast
- [ ] BUS-IMPR-8: Add "progress" indicator for the storefront while the server starts
- [ ] BUS-IMPR-9: Search results pagination — The search results page renders all matches up to a hard limit of 100 with no pagination. Category browsing pages already have pagination. Consider adding consistent pagination to search results.
- [ ] BUS-IMPR-10: Price search — Product prices are not included in the Lucene index (only name, short description, and long description are indexed). Searching for a price value (e.g., `$17.00` or `17`) only matches if that number appears in a product description. Consider adding price-range filtering or a price field to the search index.
- [ ] BUS-IMPR-11: Remove redundant "Results for 'X' (0 posters)" heading when the empty state "No products found" is displayed.
- [ ] BUS-IMPR-12: Hide the "Filters" sidebar on the search results page when there are 0 results (design flaw).
- [ ] BUS-IMPR-13: Fix Swedish catalog localization for "Panoramabilder". The category dropdown currently mixes English and Swedish ("Show All Panoramabilder").
- [ ] BUS-IMPR-14: Localize the search empty state message ("No products found", "We couldn't find anything...", "Continue Shopping") for DE, SV, and JA locales.
- [ ] BUS-IMPR-16: Add a `maxlength` attribute to the search input field to prevent excessively long queries. A reasonable limit (e.g., 200–500 characters) would prevent the 400 Bad Request at ~7000 chars and the UI layout breakage at ~1700 chars.
- [ ] BUS-IMPR-17: Long search queries (1700+ chars) cause the heading text to overflow and break the page layout. Truncate or ellipsis the displayed query in the heading.
- [ ] BUS-IMPR-18: Checkout summary column — The cart/order summary panel (subtotal, tax, shipping, total) currently only appears on the Review & Place Order page (`placeOrder.html`). It should be visible as a persistent right-hand column on all checkout steps: Shipping Address, Billing Address, and Payment. This gives customers a constant view of what they are paying throughout the checkout flow, matching e-commerce best practices (e.g., Shopify, Amazon).

## Tasks
- [ ] BUS-BUG-15: Search fails to find "Grizzly Bear" when searching for the exact partial term "grizzly" or reversed "bear grizzly", yet it finds it for "grizzly bear". (Stemmer/Analyzer defect).
- [ ] BUS-BUG-16: Pasting a full product description text into search does not return the matching product (AND operator likely failing when stop words/punctuation are present).
- [ ] BUS-BUG-17: Prefix search is inconsistent across locales. "griz" (US) and "モルフォ" (JP) return 0 results, while prefix matching works correctly in DE and SE.
- [ ] BUS-BUG-18: Stemmer over-stemming causes false positives in search. Searching for "cat" also matches products containing "catch" (e.g., "Jerusalem Artichoke" description). The English stemmer reduces "catch" → "cat".
- [ ] BUS-BUG-19: Search results heading is not localized for DE, SV, JA. It always shows `Results for '...' (N posters)` in English regardless of locale.
- [ ] BUS-BUG-20: PDP has un-localized English text in JA locale: "All posters are printed on premium, archival-quality paper with a smooth, matte or glossy finish."
- [ ] BUS-BUG-21: Usability issue with credit card input — Auto-fill or rapid entry (e.g. holding down '1') in the CC number field overflows into adjacent fields instead of stopping at the character limit. (Low priority)
- [ ] BUS-BUG-22: Search for "Hornisse" fails in de-DE locale. It returns no results or incorrect results. Expected product "Europäische Hornisse" is missing.
- [x] BUS-BUG-23: Tax calculation result is wrong — displayed value is $0.01 instead of the correct $1.44. Formula must be `tax = (subtotal + shipping) × taxRate`. Example from TC_CHK_001 execution (2026-05-18): subtotal $17.00 + shipping $7.00 = $24.00 base × 6% = **$1.44** tax → total **$25.44**. Current system shows $0.01 tax and $24.01 total. Root cause likely in the `Cart`/`Order` tax calculation service.
- [x] BUS-BUG-24: Tax rate display format is inconsistent across the checkout flow — all three pages render the rate differently and none use the required `6.00%` (two decimal places) format. Current formats: Cart page uses `${cart.taxAsString}%` (format unknown, method-driven); Order Review uses `${cart.taxRate * 100 + '%'}` (raw Java double → `6.0%`); Order Confirmation uses `#{numbers.formatDecimal(order.taxRate * 100, 1, 1)}%` (forced 1 decimal → `6.0%`). All three must be standardized to display as `6.00%` (two decimal places).
- [x] BUS-BUG-25: Tax line item display order is incorrect. Tax is correctly applied to `(subtotal + shipping)`, but because Tax is displayed *before* Shipping in the summary (`Subtotal → Tax → Shipping → Total`), it visually implies tax is calculated before shipping is known, which is misleading and technically wrong. The correct display order must be `Subtotal → Shipping → Tax → Total`, making it clear that tax is levied on the combined gross. Example: Subtotal $17.00 + Shipping $7.00 → Tax 6% = $1.44 → Total $25.44.
- [ ] BUS-IMPR-15: Searching for "a" returns 95 of 124 products. Single-letter stop words appear to be filtered by the English analyzer, preventing a full catalog search. Consider whether single-letter queries should bypass stop word filtering.
- [ ] BUS-TASK-1: Formatting: Japanese address checkout routing (Prefecture → City → Block)
- [ ] BUS-TASK-2: Logic: Yen-specific UI formatting and backend tax calculators
- [ ] BUS-TASK-3: Layout: Support for Right-To-Left / Vertical text directions 
- [ ] BUS-TASK-4: Translation: Full `ja-JP` translation of Backoffice admin UI
