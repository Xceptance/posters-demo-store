## Why

The checkout confirmation flow (`/checkout/placeOrder.html`) currently displays raw entity data which lacks visual details like product images, variation sizes, and finish textures. This creates an inconsistent and degraded user experience compared to the Shopping Cart view where rich product details are successfully mapped and rendered.

## What Changes

- Extract the cart-to-DTO conversion logic from `CartController` into a centralized `CartService` structure mapping.
- Make the `CartDto` and `CartItemDto` records publicly accessible application-wide.
- Refactor `CheckoutController` to utilize the new DTO mapping to expose rich product metadata to the template.
- Update `/checkout/placeOrder.html` to consume `CartItemDto` data and render product images seamlessly across checkout steps.

## Capabilities

### New Capabilities
- `checkout-presentation`: Requirements for the visible presentation of checkout lines items including mandatory display of images, sizes, and finishes.

### Modified Capabilities
- `shopping-cart`: Relocating presentation mapping concerns out of the Cart API and into shared Service layers.

## Impact

- **Controllers Affected**: `CartController.java`, `CheckoutController.java`
- **Services Affected**: `CartService.java`
- **Templates Affected**: `checkout/placeOrder.html`
- **Data Transfer Objects**: New `com.xceptance.posters.dto.CartDto` and `com.xceptance.posters.dto.CartItemDto` classes will be created.
