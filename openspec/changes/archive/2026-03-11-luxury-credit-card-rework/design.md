## Context

The posters-demo-store uses Spring Boot 3.x + Thymeleaf + Bootstrap 5 + HTMX 2.0.4. The checkout layout (`checkoutLayout.html`) already loads HTMX and Bootstrap JS. No SCSS pipeline exists — CSS is pre-compiled. JS is vanilla (no build step, no bundler). The current payment form (`checkout/payment.html`) is a simple Bootstrap card with form-floating inputs and no client-side logic beyond an HTML `pattern` attribute with a server-provided regex.

Entities: `CartCreditCard` stores number, vendor, name, expMonth, expYear. The `placeOrder.html` review page does not currently display card information. `CheckoutController.submitPayment()` hardcodes vendor to "Visa" and does no server-side validation.

## Goals / Non-Goals

**Goals:**
- Premium, accessible payment form with real-time vendor detection, formatted input, and inline validation
- Shared validation logic: identical rules enforced client-side (JS) and server-side (Java)
- TDD: all validation/formatting logic test-covered before implementation
- PCI-compliant masking wherever card data is re-displayed

**Non-Goals:**
- No payment gateway integration (demo store)
- No saved card management (future change)
- No form state restoration on back-navigation (future change)
- No JS build pipeline — all JS remains vanilla, loaded as static assets

## Decisions

### 1. Vendor detection via BIN prefix table (client + server)

**Decision**: Maintain a single-source BIN prefix table as a Java class (`CreditCardVendor` enum) that defines prefix ranges, valid lengths, CVV lengths, and formatting groups for all 8 vendors. The JS side duplicates this table as a plain object for client-side detection.

**Rationale**: The vendor table is small (8 entries) and changes rarely. Duplicating it avoids needing a build step or an API call just to detect vendors. A shared test fixture with known test card numbers ensures both sides stay in sync.

**Alternatives considered**:
- *Server-side only via HTMX call*: Adds latency on every keystroke. Rejected for UX reasons.
- *Generated JS from Java*: Requires a build step or template rendering of JS. Over-engineering for 8 entries.

### 2. Validation architecture: JS-first, server-mirrors

**Decision**: Client-side JS validates on blur and on submit (Luhn, length, expiry range, CVV length). Server-side mirrors all rules via a `CreditCardValidator` service class. The form submits normally (no HTMX for the primary submit flow). Optionally, an HTMX endpoint can validate individual fields on blur for enhanced UX.

**Rationale**: JS-first gives instant feedback. Server-side is the safety net (JS can be bypassed). HTMX is optional enhancement — the form works without it.

**Alternatives considered**:
- *HTMX-only validation*: Latent on every blur event. Makes the form dependent on server round-trips. Rejected.
- *No server-side validation*: Unacceptable — any form can be submitted without JS.

### 3. Combined MM/YY field with auto-slash

**Decision**: A single text input with `placeholder="MM/YY"`, `autocomplete="cc-exp"`, `inputmode="numeric"`, and `maxlength="5"`. JS inserts `/` after the second digit automatically. On submit, the value is parsed into separate month/year integers for the existing entity model.

**Rationale**: Matches modern checkout UX (Stripe, Shopify). `cc-exp` autocomplete works with the combined format. The entity model keeps separate `expMonth`/`expYear` integers, so parsing happens at the controller level.

**Alternatives considered**:
- *Keep separate dropdowns*: Functional but dated. Doesn't match the "luxury" goal.
- *Separate MM and YY text fields*: More accessible for some screen readers, but doesn't match the premium feel. Could revisit if a11y testing reveals issues with the combined field.

### 4. Number formatting with visual spacing (CSS gaps, not actual spaces)

**Decision**: Store only digits in the input's underlying value. Display grouping visually using CSS `letter-spacing` at group boundaries or by using multiple linked input segments. The simplest approach: use a single input, insert space characters for display, and strip them before submit/validation.

**Rationale**: Inserting actual space characters is the most compatible approach across browsers and autocomplete systems. Stripping is trivial (`value.replace(/\s/g, '')`).

### 5. Card masking utility (shared)

**Decision**: Create a `CreditCardMasker` utility (Java) that takes a card number and returns the standard PAN-masked format — first 6 and last 4 digits visible, middle digits replaced with asterisks (e.g., `512345******3456`). Used in:
- Controller when re-rendering payment form after validation error
- `placeOrder.html` review page
- `orderConfirmation.html` page

**Rationale**: Single utility avoids masking logic scattered across templates. PCI-DSS compliance requires consistent masking — a shared utility prevents mistakes.

### 6. No database schema changes

**Decision**: CVV is accepted as a form parameter, validated, but never persisted. The existing `CartCreditCard` entity schema is unchanged. The vendor field is populated from the auto-detection result instead of being hardcoded.

### 7. JS file structure

**Decision**: Create a single new file `static/js/credit-card.js` loaded only on the payment page via a Thymeleaf fragment or inline script block. Contains: vendor detection, formatting, auto-slash, auto-tab, validation, and error display logic.

**Rationale**: No build pipeline exists. A single focused file keeps it simple. It's only needed on one page, so it shouldn't bloat the global layout.

### 8. CSS within existing design system

**Decision**: Add payment-specific styles in a `<style>` block within the payment template or as a small `credit-card.css` static file. Use existing Bootstrap 5 utility classes and the application's established color palette / design tokens.

**Rationale**: No SCSS pipeline. Styles must match the existing design system. A small dedicated CSS file or inline styles keep it contained.

### 9. Test strategy (TDD)

**Decision**:
- **Java unit tests** (`CreditCardValidatorTest`, `CreditCardVendorTest`, `CreditCardMaskerTest`): Test Luhn, vendor detection from BIN, expiry range, CVV length, masking — all written before implementation.
- **UI tests**: Browser-level tests using the existing XLT/Selenium framework for the payment form interaction flow (type number → vendor appears → auto-format → expiry → CVV → submit → review page shows masked card).

**Rationale**: TDD for the validation logic ensures correctness before wiring up the UI. UI tests catch integration issues (JS ↔ HTML ↔ server round-trip).

## Risks / Trade-offs

- **BIN table duplication (JS + Java)** → Mitigate with shared test fixtures that validate both sides produce identical results for the same inputs.
- **Combined MM/YY field + autocomplete** → `cc-exp` is supported by major browsers, but older browsers may ignore it. Acceptable for a demo store.
- **No SCSS pipeline** → CSS changes are manual. Acceptable since the payment-specific styles are small and self-contained.
- **Auto-tab can confuse some users** → Only triggers when field is complete (all digits entered). Users can still manually tab. Screen reader users may find it unexpected — document with ARIA live regions.
