## 1. Core Validation & Detection (Java — TDD)

- [x] 1.1 Create `CreditCardVendor` enum with BIN prefix ranges, valid lengths, CVV lengths, and formatting groups for all 8 vendors
- [x] 1.2 Write `CreditCardVendorTest` — test vendor detection from BIN prefixes for all vendors and unknown prefixes
- [x] 1.3 Write `CreditCardValidatorTest` — test Luhn checksum (valid/invalid), vendor-specific length, expiry range (past, future, edge cases), CVV length (3 vs 4)
- [x] 1.4 Create `CreditCardValidator` service class implementing Luhn, length, expiry, and CVV validation — make tests pass
- [x] 1.5 Write `CreditCardMaskerTest` — test PAN masking (first 6 + last 4 visible, middle asterisks) for various card lengths
- [x] 1.6 Create `CreditCardMasker` utility — make tests pass

## 2. Server-Side Integration

- [x] 2.1 Update `CheckoutController.submitPayment()` to accept CVV parameter, parse MM/YY expiry into month/year, and auto-detect vendor instead of hardcoding "Visa"
- [x] 2.2 Wire `CreditCardValidator` into controller — reject invalid submissions with field-level error messages and re-render form
- [x] 2.3 Apply PAN masking via `CreditCardMasker` when re-rendering payment form after validation error
- [x] 2.4 Display masked card number on `placeOrder.html` order review page
- [x] 2.5 Display masked card number on `orderConfirmation.html` page
- [x] 2.6 Ensure CVV is never persisted or re-displayed — validate only

## 3. Client-Side JavaScript

- [x] 3.1 Create `static/js/credit-card.js` — vendor detection from BIN prefix table (mirror of Java enum)
- [x] 3.2 Implement card number auto-formatting (4-4-4-4, 4-6-5 Amex, 4-6-4 Diners) with space insertion and paste handling
- [x] 3.3 Implement combined MM/YY input with auto-slash insertion and backspace handling
- [x] 3.4 Implement auto-tab progression: card number → expiry → CVV on field completion
- [x] 3.5 Implement client-side validation on blur and submit: Luhn, length, expiry, CVV — with inline error messages
- [x] 3.6 Strip spaces from card number and parse expiry before form submission
- [x] 3.7 Clear inline errors on blur when field is corrected

## 4. Payment Page Template & Styling

- [x] 4.1 Rework `checkout/payment.html` — restructure form with card number, cardholder name, combined MM/YY, and CVV fields
- [x] 4.2 Add dynamic vendor logo/icon area with smooth transition animations
- [x] 4.3 Add "Secure Payment" trust badge with lock icon
- [x] 4.4 Implement floating labels and premium styling (dark-accent card feel, subtle animations)
- [x] 4.5 Create `static/css/credit-card.css` (or inline style block) — luxury styles consistent with application design system
- [x] 4.6 Load `credit-card.js` only on the payment page via Thymeleaf block

## 5. Autocomplete, Mobile & Accessibility

- [x] 5.1 Add `autocomplete` attributes: `cc-number`, `cc-name`, `cc-csc`, `cc-exp` on respective fields
- [x] 5.2 Add `inputmode="numeric"` on card number, CVV, and expiry fields
- [x] 5.3 Add ARIA labels on all payment fields
- [x] 5.4 Add ARIA live region for vendor detection announcements
- [x] 5.5 Implement focus management after validation errors (focus first invalid field)
- [x] 5.6 Verify full keyboard-only navigation (Tab/Shift-Tab, Enter/Space)

## 6. HTMX Inline Validation (Optional)

- [ ] 6.1 Create server-side validation endpoint for individual field validation — _deferred (optional)_
- [ ] 6.2 Wire HTMX `hx-post` on blur for payment fields to call the validation endpoint — _deferred (optional)_
- [ ] 6.3 Return inline error HTML fragments from the endpoint — _deferred (optional)_

## 7. I18n & Messages

- [x] 7.1 Add/update i18n message keys for new labels, placeholders, and error messages (card number, CVV, expiry, vendor names)
- [x] 7.2 Verify messages render correctly in all supported locales

## 8. Back-Navigation

- [x] 8.1 Ensure navigating back from order review to payment clears all fields (fresh empty form)

## 9. Test Cards & Documentation

- [x] 9.1 Create a reference list of valid test card numbers (one per vendor, Luhn-valid) for demo/testing use

## 10. UI & Integration Testing

- [ ] 10.1 Write browser-level UI tests: type card number → vendor logo appears → number formats correctly → expiry auto-slash → CVV → submit → review page shows masked card — _deferred to browser testing phase_
- [ ] 10.2 Test copy-paste of card numbers with spaces/dashes — _deferred to browser testing phase_
- [ ] 10.3 Test form submission with JS disabled (server-side validation catches errors) — _deferred to browser testing phase_
- [ ] 10.4 Test back-navigation yields empty form — _deferred to browser testing phase_

