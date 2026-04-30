# Shopping Cart

## Purpose
TBD - Manages the user's shopping cart, adding items, tracking quantities, and calculating checkout totals.

## Requirements

### Requirement: Uniform Add to Cart Validation
The system MUST enforce identical security checks and business rules for adding items to the cart, regardless of whether the request originates from a browser HTTP client or a headless JSON client.

#### Scenario: Enforce identical quantity cap
- **WHEN** an AI Agent or User requests to add 150 items to a cart
- **THEN** the system caps the total quantity added at 99 items, regardless of the API endpoint used

### Requirement: Cart Price Persistence
The system SHALL persist the price and product name snapshot (`unitPrice` and `productName`) directly into the `CartLineItem` entity when an item is added to the cart, instead of relying on dynamic lookups during checkout. This guarantees that the final order price precisely matches the cart subtotal at the time of addition.

#### Scenario: Price remains consistent through checkout
- **WHEN** a customer adds an item to their cart at a specific price point
- **THEN** the system persists that exact unit price into the `CartLineItem` and retains it through the entire checkout flow so the final `CatalogOrder` accurately reflects the originally quoted price, even if catalog prices change mid-checkout.
