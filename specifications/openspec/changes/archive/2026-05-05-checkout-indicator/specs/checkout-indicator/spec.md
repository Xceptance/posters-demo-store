## ADDED Requirements

### Requirement: Checkout Indicator Rendering
The system SHALL render a visual progress indicator at the top of the checkout flow to orient the user.

#### Scenario: Displaying the indicator steps
- **WHEN** the user navigates to any step in the checkout process
- **THEN** the checkout indicator is displayed above the main content area
- **AND** the indicator shows the 5 steps connected by a gray line:
  1. Shipping Address
  2. Billing Address
  3. Payment
  4. Review & Place Order
  5. Order Confirmation

### Requirement: Active Step Highlighting
The checkout indicator SHALL visually highlight the current step the user is on, with specific styling for past, present, and future steps.

#### Scenario: Active step styling
- **WHEN** the user is on a specific step
- **THEN** the active step is rendered as a spherical dot with a blue background and white text containing the step number
- **AND** it is visually highlighted

#### Scenario: Past (taken) step styling
- **WHEN** there are steps before the currently active step
- **THEN** those taken steps are rendered in a grayish color

#### Scenario: Future (next) step styling
- **WHEN** there are steps after the currently active step
- **THEN** those next steps are rendered without numbers and just a round border
