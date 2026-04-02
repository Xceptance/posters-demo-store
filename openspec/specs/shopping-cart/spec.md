# Shopping Cart

## Purpose
TBD - Manages the user's shopping cart, adding items, tracking quantities, and calculating checkout totals.

## Requirements

### Requirement: Uniform Add to Cart Validation
The system MUST enforce identical security checks and business rules for adding items to the cart, regardless of whether the request originates from a browser HTTP client or a headless JSON client.

#### Scenario: Enforce identical quantity cap
- **WHEN** an AI Agent or User requests to add 150 items to a cart
- **THEN** the system caps the total quantity added at 99 items, regardless of the API endpoint used
