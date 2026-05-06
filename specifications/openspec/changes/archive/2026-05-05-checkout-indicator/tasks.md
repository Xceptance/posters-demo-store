## 1. Frontend Structure

- [x] 1.1 Create `checkout-indicator.html` Thymeleaf fragment in `templates/fragments/`
- [x] 1.2 Implement the HTML structure for the 5 steps: Shipping Address, Billing Address, Payment, Review & Place Order, and Order Confirmation

## 2. Styling

- [x] 2.1 Add CSS rules to render steps as spherical dots connected by a gray line
- [x] 2.2 Add CSS rules for the active state: blue background, white text, with step number
- [x] 2.3 Add CSS rules for the past (taken) state: grayish color with step number
- [x] 2.4 Add CSS rules for the future (next) state: round border without numbers
- [x] 2.5 Ensure the indicator is responsive and displays correctly on mobile devices

## 3. Integration

- [x] 3.1 Include the `checkout-indicator` fragment in the Shipping Address template and pass the active step
- [x] 3.2 Include the `checkout-indicator` fragment in the Billing Address template and pass the active step
- [x] 3.3 Include the `checkout-indicator` fragment in the Payment template and pass the active step
- [x] 3.4 Include the `checkout-indicator` fragment in the Review & Place Order template and pass the active step
- [x] 3.5 Include the `checkout-indicator` fragment in the Order Confirmation template and pass the active step

## 4. Testing & Verification

- [x] 4.1 Run existing checkout UI tests to ensure they do not fail due to layout shifts
- [x] 4.2 Add verification steps in functional tests to assert the correct visual states of the checkout indicator

## 5. Localization

- [x] 5.1 Add translation keys (e.g., `checkout.indicator.step.shipping`, etc.) to the default `messages.properties`
- [x] 5.2 Add translation keys to all other supported language bundles (e.g., `messages_de.properties`)
- [x] 5.3 Implement `#{...}` expressions in the Thymeleaf fragment instead of hardcoded labels
