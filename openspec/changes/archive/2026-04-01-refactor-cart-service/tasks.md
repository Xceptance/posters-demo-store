## 1. Create Core Service Layer

- [x] 1.1 Create a new `CartService.java` inside `com.xceptance.posters.service`.
- [x] 1.2 Extract the `findVariant` and `lookupPrice` helper logic from `CartController.java` into the `CartService` (or `CatalogService`). Ensure parameters and variables are structured to use the `final` modifier wherever possible.
- [x] 1.3 Implement the `addProductToCart` method inside `CartService`. This should handle variant logic, quantity additions, the 99-item limit, and line item creation/updates.
- [x] 1.4 Move `recalculateTotals` from `CartController` into `CartService` and use `final` on intermediate variables.

## 2. Refactor CartController

- [x] 2.1 Pass `CartService` via constructor injection into `CartController`. Ensure the field is marked `final`.
- [x] 2.2 Refactor the WebMCP JSON endpoint (`addJsonToCart`) to delegate logic to `cartService.addProductToCart(...)`. Apply `final` to method parameters and local variables.
- [x] 2.3 Refactor the HTMX endpoint (`addToCartSlider`) to delegate logic to `cartService.addProductToCart(...)`. Apply `final` modifiers there as well.
- [x] 2.4 Review the entirety of `CartController.java` to apply "finals where possible" as requested (e.g., catching previously existing non-final variables).

## 3. Verification

- [x] 3.1 Verify `WebMcpIntegrationTest` and other relevant tests still pass with zero modifications.
- [x] 3.2 Verify that the strict 99 cap validation rule naturally applies via the newly routed service method across both the HTTP and WebMCP API channels.
