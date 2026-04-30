# Charter: Guest Checkout - Edge Cases

**Coverage Area:** checkout
**Tags:** guest, edge-cases, validation
**Status:** Unexplored

## 1. Mission
*What is the main goal of this session?*
Explore the guest checkout flow to identify form validation bugs, logic errors, and edge cases when interacting with the checkout forms or manipulating state.

## 2. Area / Scope
*Which specific features or components are to be tested?*
- Form validation boundary testing on Shipping, Billing, and Payment steps
- Cart manipulation during checkout
- Network and session interruptions

## 3. Setup / Environment / Data
*Any specific accounts, data payloads, or environment configs needed before starting?*
- Clean browser session (Incognito/Private mode recommended).
- No active user session (ensure the user is explicitly logged out).
- Pre-condition: Add at least one item to the cart before beginning the flow.

## 4. Test Focus / Strategy
*What specific angles or quality criteria will guide the exploration?*
- **Form Validation & Boundary Testing:** Submit empty fields, invalid email formats, malformed zip codes, excessively long names/addresses, and script injection attempts.
- **Cart Manipulation:** Attempt to open the cart in another tab, change item quantities, or remove items while halfway through the checkout process.
- **Payment Edge Cases:** Input invalid credit card numbers, expired dates, or missing CVVs to verify error handling logic.
- **Session Expiry:** Let the session expire midway through checkout and attempt to submit the form to see if the state recovers or fails gracefully.
