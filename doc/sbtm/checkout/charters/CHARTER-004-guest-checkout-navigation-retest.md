# Charter: Guest Checkout - Navigation Retest

**Coverage Area:** checkout
**Tags:** guest, navigation, browser-history, regression
**Status:** Unexplored (Blocked pending fixes)

## 1. Mission
*What is the main goal of this session?*
Perform a deep-dive exploration of the checkout flow's navigation state, specifically focusing on complex back/forward paths to ensure state preservation without security leaks.

## 2. Area / Scope
*Which specific features or components are to be tested?*
- Form state preservation across all checkout steps (Shipping, Billing, Payment, Review).
- Browser-native "Back" and "Forward" buttons.
- Multi-step jumps (e.g., jumping two steps back).
- Form data masking (ensuring CVV and full CC numbers do not reappear on back-navigation).

## 3. Setup / Environment / Data
*Any specific accounts, data payloads, or environment configs needed before starting?*
- Ensure the defects found in `SESSION-20260417-CHARTER-001` (specifically the CVV retention bug) have been deployed to the test environment.
- Clean browser session.

## 4. Test Focus / Strategy
*What specific angles or quality criteria will guide the exploration?*
- **Browser History Manipulation:** Heavily rely on the browser's native forward/back arrows rather than the application UI buttons.
- **Multi-step Reversal:** From the Order Review page, hit 'Back' two or three times rapidly to see if state loads correctly or if it causes errors.
- **Security Verification:** explicitly verify that Payment Information (CC / CVV) is properly masked or cleared when navigating backwards.
