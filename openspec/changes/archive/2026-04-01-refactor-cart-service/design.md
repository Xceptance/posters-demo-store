## Context

Currently, the Posters Demo Store has two ways to add products to the cart:
1. HTMX UI: The `addToCartSlider` endpoint in `CartController.java` handles browser-based interactions, searching for variants, creating cart line items, and returning HTML fragments.
2. WebMCP API: The `addJsonToCart` endpoint provides a JSON API for AI tools to add items. It also performs variant matching, line item updates, and additionally enforces a maximum quantity limit (99) and string length bounds on inputs.

Because this logic is duplicated directly inside the controller methods, it violates DRY and has already led to inconsistent security checks. 

## Goals / Non-Goals

**Goals:**
- Extract the business logic for adding items to the cart into a reusable `CartService`.
- Make `CartController` a "thin" controller that only handles HTTP routing, DTO mapping, and returning the correct view (JSON or HTMX fragment).
- Apply the 99-item quantity limit consistently across all cart additions.

**Non-Goals:**
- We are *not* changing the HTTP routing or unifying the endpoints. We will maintain the separate `/api/v2/cart/add` and `/{locale}/addToCartSlider` paths.
- We are not changing the frontend UI or how HTMX functions.
- We are not changing the WebMCP tool schema.

## Decisions

- **Create `CartService`**: A new Spring `@Service` will be created with a method like `addProductToCart(Cart, Product, String finish, String size, int quantity)`.
- **Validation in Service**: The size bounds and quantity cap (current max: 99) will be executed inside the Service layer so that neither the HTMX nor WebMCP layers can bypass them.

## Risks / Trade-offs

- **Risk**: Refactoring core cart logic could break the checkout flow if variant matching logic is migrated incorrectly.
- **Mitigation**: Existing unit and integration tests (like `WebMcpIntegrationTest`) should pass without modification, as the external observable behavior and responses are completely unchanged.
