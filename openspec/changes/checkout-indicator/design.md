## Context

The Posters Demo Store currently lacks a visual progress indicator during the checkout flow. The user navigates through multiple steps (1. Shipping Address, 2. Billing Address, 3. Payment, 4. Review & Place Order, 5. Order Confirmation) but is not explicitly shown their current position in the sequence. To improve user experience and reduce friction, we are adding a visual checkout indicator.

## Goals / Non-Goals

**Goals:**
- Provide a clear, visual representation of the checkout steps.
- Highlight the current step dynamically based on the active page.
- Create a reusable UI component that fits within the existing layout.

**Non-Goals:**
- Modifying the underlying backend checkout logic or domain models.
- Altering the data submitted during the checkout process.

## Decisions

- **UI Implementation**: We will implement the checkout indicator as a Thymeleaf fragment (e.g., `templates/fragments/checkout-indicator.html`). This allows us to include it easily in the relevant checkout templates (`checkout-shipping.html`, `checkout-payment.html`, `checkout-overview.html`).
- **Styling**: The steps will be rendered as spherical dots connected by a gray line. The active step is highlighted with a blue background and white text. Future steps will be displayed with a round border but without numbers. Taken steps will be rendered with a grayish color. Custom CSS scoped to `.checkout-indicator` will be used alongside Bootstrap 5 utilities to achieve this exact appearance.
- **State Management**: The current step will be determined by passing a variable (e.g., `step='shipping'`) to the Thymeleaf fragment from the including template, or by checking the current URL/view context.
- **Localization**: Step labels must not be hardcoded in HTML. They will be externalized to Spring message bundles (`messages.properties`) and injected via Thymeleaf `#{...}` syntax to support multi-language environments seamlessly.

## Risks / Trade-offs

- **Risk**: Fragment inclusion might break the existing checkout layout if not placed correctly within the grid.
  - **Mitigation**: We will integrate the fragment carefully within the main container of the checkout layout, ensuring it sits above the main content area but below the header, utilizing appropriate margin/padding classes.
- **Risk**: Mobile responsiveness issues with a horizontal progress bar.
  - **Mitigation**: Use flexbox and text truncation or icon-based steps on smaller viewports to ensure it remains legible on mobile devices.
