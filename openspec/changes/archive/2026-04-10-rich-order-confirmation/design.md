## Context

Order confirmation currently renders using a naked `CatalogOrder` database entity. Consequently, critical presentation attributes derived during shopping—such as formatted sizes, finishes, and catalog image URLs—are dropped because `CatalogOrder` structurally stores line items (`OrderLineItem`) purely as financial log snapshots (amount, raw sku, base name). This discrepancy breaks visual continuity for the customer immediately post-purchase.

The proposed solution extracts an `OrderDto` mapped specifically for rendering. Additionally, we use feedback to structurally inject the `imageURL` and `variantDescription` onto the `CatalogOrder` table snapshot at the moment of checkout, hardening historical data resilience.

## Goals / Non-Goals

**Goals:**
- Render order confirmation with identical rich metrics as the cart layout without querying the current catalog blindly (to protect against future catalog deletion defects).
- Abstract layout processing into immutable Data Transfer Objects (`OrderDto`, `OrderItemDto`).
- Support immediate re-use of these DTO structures within the user's Account Order History UI pipeline.

**Non-Goals:**
- Completely rewriting `CheckoutController`'s database saving logic. Total logic preservation is critical; we solely modify DTO exposure for templates.
- Making `OrderLineItem` prices dynamic. Prices MUST remain snapshot-static.

## Decisions

- **Decision 1: `OrderDto` Implementation** - We will create a standalone `OrderDto` mapped explicitly via a new service or directly within `CheckoutService.java`. We chose standalone structs over inner classes to prevent Spring validation binding errors.
  * *Alternative Considered*: Fetching `CartDto` again using identical `sku`. *Rejected* because it binds historical order interfaces to current catalog structures, crashing explicitly if a SKU is sunset.
- **Decision 2: Modifying `CartToOrderConverter`** - We will capture dynamically-generated image URLs using `CartService.toCartItemDto` during conversion, explicitly mapping those outputs onto the `OrderLineItem` tables permanently.
  * *Alternative Considered*: Computing images solely in the `OrderDto` map. *Rejected* because persisting it manually creates definitive archival sovereignty.

## Risks / Trade-offs

- [Risk] Null Pointer Exceptions during mapping -> Mitigation: Employ strict nullable checks mimicking `CartItemDto`, resorting to `/images/placeholder.jpg` when necessary.
- [Risk] Database column constraint issues -> Mitigation: The schema for `catalog_order_lineitems` natively maps `image_url` and `variant_description`; we merely invoke their setters.
