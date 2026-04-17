## ADDED Requirements

### Requirement: Include Checkout Indicator
The checkout presentation layer SHALL include the visual checkout indicator fragment in all checkout steps.

#### Scenario: Checkout view rendering
- **WHEN** the system renders any checkout step template (shipping, billing, payment, overview, or confirmation)
- **THEN** it incorporates the `checkout-indicator` fragment above the primary checkout content
- **AND** passes the correct current step identifier to the fragment
