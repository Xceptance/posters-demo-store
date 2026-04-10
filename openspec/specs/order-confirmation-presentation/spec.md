## ADDED Requirements

### Requirement: Rich Order Presentation
The system SHALL display an exact duplicate of the user's cart item visual layout (including images, size variants, and finish descriptions) during the order confirmation and identical downstream order history endpoints.

#### Scenario: Order Confirmation Render
- **WHEN** the customer views the `/checkout/orderConfirmation` gateway after a successful purchase
- **THEN** the layout renders a mapped `OrderDto` where every child `OrderItemDto` contains its catalog image and custom variant string populated

### Requirement: Data Storage Sovereignty
The `CartToOrderConverter` SHALL hard-commit variant text and catalog URLs to the `catalog_order_lineitems` table to shield order histories from downstream catalog restructuring crashes.

#### Scenario: Permanent Snapshot Persistence
- **WHEN** the `CartToOrderConverter.convert()` method executes to finalize a cart into an order
- **THEN** the system fetches dynamically-resolved image targets from `CartService` and writes them into `image_url` and `variant_description` columns on the `OrderLineItem` entity.
