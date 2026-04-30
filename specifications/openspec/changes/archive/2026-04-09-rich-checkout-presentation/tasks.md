# Implementation Tasks

## Phase 1: DTO Domain Restructuring
-[x] Create `com.xceptance.posters.dto.CartDto` file and extract the record definition from `CartController`.
-[x] Create `com.xceptance.posters.dto.CartItemDto` file and extract the record definition out of `CartController`.
-[x] Refactor imports inside `CartController` to point to the new DTO paths.
-[x] Run `mvn test` to guarantee moving the DTO records did not cause compiler or layout integration failures.

## Phase 2: Service Layer Migration
-[x] Move `toCartDto` and `toCartItemDto` functions from `CartController` to `CartService`.
-[x] Update `CartService.java` imports and autowire/inject `LocalizedTextService` and Catalog repositories correctly if not already present.
-[x] Update `CartController` endpoints (`viewCart`, `viewMiniCart`, etc.) to invoke `cartService.toCartDto(..)` instead of the old private methods.

## Phase 3: Checkout Presentation Overhaul
-[x] Autowire/Inject `CartService` into `CheckoutController`.
-[x] Modify `CheckoutController.placeOrder` to format the `CatalogCart` session object via `cartService.toCartDto(...)` and attach it as `cart` inside the Model mapping.
-[x] Refactor `src/main/resources/templates/checkout/placeOrder.html` to iterate over `${cart.products}` instead of `${cart.lineItems}`.
-[x] Update `placeOrder.html` grid to embed rendering logic for `<img th:src="@{${cp.imageURL}}">`.
-[x] Bind HTML variables for `${cp.finish}` and `${cp.sizeLabel}` into `placeOrder.html`.

## Phase 4: Automated Testing Coverage
-[x] **Unit Tests:** Add comprehensive DTO mapping tests to `CartServiceTest` validating image strings and missing catalog variant fallbacks dynamically map as expected.
-[x] **Integration GUI Tests:** Add `testPlaceOrderRendersRichCartItems` into `CheckoutControllerUiTest.java` that validates image references load properly.
-[x] Run full test suite regression `mvn test` to guarantee Cart, WebMCP APIs, and Checkout flows coexist perfectly.
