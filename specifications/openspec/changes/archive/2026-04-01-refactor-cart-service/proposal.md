## Why

The current HTMX (`addToCartSlider`) and JSON/WebMCP (`addJsonToCart`) endpoints in `CartController.java` duplicate core business logic for adding items to the cart. This includes variant matching, inventory/quantity limits, price lookups, and cart total recalculations. This violates DRY principles and creates a risk of inconsistent behavior between AI agents and human users (e.g., currently, WebMCP has a security cap of 99 items, which HTMX lacks).

## What Changes

- Extract core cart handling logic from `CartController.java` into a new `CartService.java`.
- Refactor both the HTMX endpoint and the WebMCP JSON endpoint to utilize the exact same service method for adding items to the cart.
- Standardize security and validation rules (like maximum quantity limits and ReDOS string length protections) across all input channels.

## Capabilities

### New Capabilities
None. This is an architectural refactoring with no new user-facing features.

### Modified Capabilities
- `shopping-cart`: Modifying implementation of Add to Cart logic to use a unified service layer, ensuring consistent validation. Requirements remain functionally identical for end clients.

## Impact

- Architecture: Introduces a dedicated Service layer for cart operations.
- Code: Heavy changes to `CartController.java` to make it a "thin" controller. Creation of `CartService.java`.
- Risk: Low to Medium. Existing clients (Thymeleaf templates and JSON agents) will receive the identical response payloads and HTML fragments as before, but the underlying execution path will change.
