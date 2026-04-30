## Context

The `CheckoutController` injects the raw `CatalogCart` entity into the `checkout/placeOrder.html` template. While this provides pricing and SKU data, it leaves out crucial display-oriented metadata such as `imageURL`, mapped `sizeLabel`, and localized product designations because the Entity relies strictly on raw string variants and IDs, whereas DTOs resolve these against full catalog objects. `CartController` already solved this context problem perfectly with its nested `CartDto` mapping functions.

## Goals / Non-Goals

**Goals:**
- Extract the nested domain logic out of `CartController`.
- Provide a standardized, unified `toCartDto` mapping operation in `CartService`.
- Expose rich product metadata globally across standard layout controllers.
- Resolve missing image/size bindings in `/checkout/placeOrder`.

**Non-Goals:**
- Do not refactor how `GuestCheckout` or Cart storage primitives work; we exclusively seek to alter the presentation schema.

## Decisions

1. **Extract `CartDto` & `CartItemDto` to Core packages**: We'll extract `CartDto` out of `CartController` into our standard `com.xceptance.posters.dto` package since it models reusable presentation logic perfectly.
2. **Shift Presentation Logic to `CartService`**: Moving `toCartDto(...)` from the Controller layer to `CartService` honors MVC architecture natively—services structure objects for views, and controllers bind them.
3. **Template alignment**: Replace `cp : ${cart.lineItems}` inside `checkout/placeOrder.html` with `cp : ${cart.products}` matching the schema exposed by the newly adopted DTO array.
4. **Automated Validation**: We will add a new test in `CheckoutControllerUiTest.java` that formally loads `/checkout/placeOrder` and asserts that product images render physically in the HTML document.

## Risks / Trade-offs

- **Risk**: Refactoring shared `CartDto` might unexpectedly alter how the Mini-cart or HTMX headers render.
  - **Mitigation**: We'll enforce a strict copy-paste of the legacy record definition and conduct localized structural verification UI tests via `mvn test`. We will also add missing explicit UI rendering tests for the place order view.
