## Why

The current credit card payment screen is functionally minimal — a plain form with a regex-validated card number, name, and two separate dropdowns for month/year. There is no CVV field, no card vendor detection (vendor is hardcoded to "Visa"), no client-side validation beyond HTML `pattern`, and no visual feedback during entry. This falls short of a modern, trustworthy checkout experience.

This change reworks the payment screen to feel premium and secure: real-time card vendor detection with branding, grouped number formatting, a combined MM/YY expiry field, a CVV field, and proper Luhn checksum + expiry validation — both client-side (instant feedback) and server-side (structural validation). HTMX can optionally bridge the two for a seamless inline validation flow.

**Non-goals**: This is a demo store. There are no real payment gateway API calls, no transaction processing, and no fraud detection. All validation is structural (format, checksum, expiry) — not transactional. Saved card management for logged-in users is deferred to a future change.

**Process**: This change follows a **TDD (Test-Driven Development)** approach. For all validation and formatting logic, tests are written first (defining expected behavior per vendor, edge cases, and error states), then the implementation is written to make the tests pass.

## What Changes

- **Card vendor detection**: Detect Visa, Mastercard, Amex, UnionPay, JCB, Discover, Diners Club, and Maestro from the first digits (BIN prefixes) and display the vendor logo/icon dynamically as the user types. The "Name on Card" field remains; vendor is auto-detected, not user-selected. When digits don't match any known vendor, display a neutral/generic state (no vendor icon highlighted) — not an error.
- **Number formatting**: Auto-format the card number in groups (4-4-4-4 for most cards, 4-6-5 for Amex, 4-6-4 for 14-digit Diners Club) with visual spacing. Must handle copy-paste gracefully (strip spaces, dashes, and non-digits from pasted input before formatting). On submit, digits are stripped client-side before POST; the server also strips non-digits defensively.
- **Combined expiry field**: Replace the two separate month/year dropdowns with a single `MM/YY` text input with placeholder, auto-slash, and date validation (not in the past, max current year + 20).
- **CVV field**: Add a CVV/CVC input with vendor-aware length enforcement (3 digits for most, 4 for Amex). CVV is validated but **never stored** in the database.
- **Client-side validation**: Luhn checksum, expiry date check, CVV length, card number length per vendor — all with inline error messages before form submission. Errors clear on blur when the field is corrected.
- **Auto-tab field progression**: Automatically advance focus from card number → expiry → CVV as each field reaches its expected length.
- **Server-side validation**: Mirror all validation rules on the backend (`CheckoutController` / new validation service) so bypassing JS doesn't skip checks.
- **PCI-compliant masking**: When re-displaying the form after a server-side validation error, mask the card number using the standard PAN format — first 6 and last 4 digits visible, middle digits replaced with asterisks (e.g., `512345******3456`). CVV is never re-displayed. Card number on the order review/confirmation page must also be masked.
- **HTMX integration** (optional): Use HTMX to call a server-side validation endpoint on blur/change, providing unified feedback without a full page reload.
- **Browser autocomplete**: Use proper `autocomplete` attributes (`cc-number`, `cc-name`, `cc-csc`, `cc-exp`) to support browser and password manager autofill.
- **Mobile input**: Use `inputmode="numeric"` on card number, CVV, and expiry fields to trigger the numeric keyboard on mobile devices.
- **Accessibility**: ARIA labels on all fields, focus management after validation errors, screen reader announcements for vendor detection changes, full keyboard-only navigation support.
- **Back-navigation**: Returning to the payment page from order review starts with a fresh empty form (state restoration deferred to a future change).
- **Luxury UI polish**: Premium card-entry styling — subtle animations, vendor logo transitions, floating labels, dark-accent card preview feel. Includes a visual "Secure Payment" trust badge with lock icon for confidence. All styling and design must match the existing overall design system of the application.
- **Test card documentation**: Provide a reference list of valid test card numbers per vendor for demo/testing use.

## Capabilities

### New Capabilities

- `credit-card-validation`: Client-side and server-side validation rules — Luhn checksum, vendor-specific number length, expiry date range, CVV length. Includes vendor detection from BIN prefixes for 8 vendors: Visa, Mastercard, Amex, UnionPay, JCB, Discover, Diners Club, and Maestro.
- `credit-card-ux`: Premium payment form UX — auto-formatting card numbers in groups, combined MM/YY expiry input, dynamic vendor logo display, inline validation feedback, copy-paste handling, browser autocomplete, mobile numeric input, accessibility, and luxury styling.

### Modified Capabilities

_(none — no existing specs are affected)_

## Impact

- **Templates**: `checkout/payment.html` — major rework of the form structure and addition of JS-driven UI. Also `checkout/placeOrder.html` — ensure card is displayed masked.
- **Controller**: `CheckoutController.submitPayment()` — accept CVV for validation (not persisted), parse MM/YY expiry, run server-side validation.
- **New service/utility**: Credit card validation logic (Luhn, vendor detection, expiry check) — reusable from both JS and Java.
- **JS/CSS assets**: New or extended JavaScript for formatting, validation, and paste handling; SCSS for premium styling.
- **Messages**: Updated i18n keys for new labels, placeholders, and error messages.
- **Tests (unit)**: Unit tests for all validation logic (Luhn, vendor detection, expiry, CVV length) and formatting (grouping, paste handling) — written first per TDD.
- **Tests (UI)**: Browser-level tests for the payment form — vendor logo appears on typing, number groups correctly, MM/YY auto-slash works, validation errors display inline, masked card shows on review page, and form submits successfully.
- **Documentation**: Test card number reference for demo usage.
- **Database**: No schema changes expected.
