## Why

The checkout process currently lacks visual feedback regarding the user's progress through the multi-step flow. Customers may become confused about how many steps remain (e.g., Shipping, Payment, Review), leading to potential cart abandonment or friction. Introducing a clear, visual checkout indicator will improve the UX by setting expectations and providing context.

## What Changes

- Implement a visual checkout progress indicator at the top of the checkout pages.
- The indicator will highlight the current step in the process (1. Shipping Address, 2. Billing Address, 3. Payment, 4. Review & Place Order, 5. Order Confirmation).
- The indicator should look modern and fit the existing design system.
- It will be integrated into the main checkout template or layout fragment so it persists across the checkout flow.

## Capabilities

### New Capabilities
- `checkout-indicator`: Defines the visual presentation and behavior of the checkout progress indicator across the checkout flow.

### Modified Capabilities
- `checkout-presentation`: The main checkout presentation is modified to include the new indicator at the top of the content area.

## Impact

- **UI/Templates**: The checkout layout and relevant Thymeleaf templates will be updated to include the indicator fragment.
- **CSS/Styles**: New styles will be added for the progress bar, step indicators, and active/completed states.
- **Tests**: Functional tests verifying the checkout presentation will need to be updated to account for the presence of the progress indicator.
