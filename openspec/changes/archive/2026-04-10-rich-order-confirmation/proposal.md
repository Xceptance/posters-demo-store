## Why

The current order confirmation page renders ordered items as a basic text list containing only the product name, SKU, price, and quantity. Customers cannot visually verify the customizations they selected (size, finish) or review the product image once the transaction completes. Upgrading this layout guarantees a consistent, rich visual presentation across the entire shopping funnel (Cart -> Checkout -> Order Confirmation), improving customer trust and preventing post-purchase anxiety.

## What Changes

- Redesign the `checkout/orderConfirmation.html` template to render product images alongside ordered items.
- Enhance the items loop to visually extract and format variant descriptions (such as physical `size` and `finish`).
- Decouple the presentation layer completely from the `CatalogOrder` entity by creating an intermediate `OrderDto` (and `OrderItemDto`) mapping. This mirrors the shopping cart architecture and paves the way for identical UI rendering inside the future Account Order History portal.
- Integrate formatting resolution into the order converter lifecycle to capture missing metadata (`imageURL`, `variantDescription`) at the exact point of snapshot creation, persisting it natively into the `OrderLineItem` database table.
- **BREAKING**: None. Schema tables for `catalog_order_lineitems` already contain dormant columns for `image_url` and `variant_description`; this update simply populates and queries them.

## Capabilities

### New Capabilities
- `order-confirmation-presentation`: Standardizes the visual presentation architecture for post-checkout order summary pages, ensuring full media and variant resolution identical to pre-checkout cart behavior.

### Modified Capabilities
- `<empty>`: There are no existing specs fundamentally changing.

## Impact

- `CartToOrderConverter`: Signature update or internal service coupling to map dynamic image strings at snapshot time.
- `CheckoutService` / `OrderService`: Affected by potential `CartService` dependency injection.
- Template `orderConfirmation.html`: Layout modifications.
