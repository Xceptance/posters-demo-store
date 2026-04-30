## Why

The current demo store relies on a flat, single-file DB approach with an inflexible entity model. To enable modern e-commerce features—such as multi-region sites, explicit tax/pricing rules, global shipping methods, and a strict 3-tier product catalog (Master -> Variant -> SKU)—the entity model requires a complete overhaul. This change will establish a robust, normalized relational schema that supports these capabilities while ensuring immutable order snapshots for compliance and history.

## What Changes

- **BREAKING**: Replace the current flat product catalog with a 3-tier hierarchy (`products` -> `variants` -> `skus`).
- **BREAKING**: Replace the current category structure with a maximum 2-level deep self-referencing `categories` table.
- Introduce `sites` as the root configuration for locales, currencies, price tables, tax tables, and shipping methods.
- Implement explicit `price_tables` and `tax_tables` to support multi-region pricing without cross-locale fallback.
- Implement global `variation_attributes` (e.g., "Color") and link them to products via a join table.
- Implement basic `inventory_tables` and `inventory_entries` for transactional stock counting.
- Completely redesign the checkout and order flow to use temporary cart entities (`cart_addresses`, `cart_credit_cards`) and strictly immutable `order` snapshots.
- Update the SKU string format to `[A-Z0-9]{6,10}-[0-9]{4}`.

## Capabilities

### New Capabilities
- `catalog-management`: The 3-tier product catalog, categories, and global variation attributes.
- `site-configuration`: The root site setup linking locales, currency, pricing, and taxes.
- `pricing-and-tax`: Multi-region price tables and tax rate management.
- `inventory`: Basic transactional stock counting.
- `checkout-and-orders`: Immutable order snapshots and temporary cart entities.
- `customer-profiles`: User accounts, saved addresses, and masked credit cards.
- `data-migration`: Initial setup data import from XML files (replacing single-file approach).

### Modified Capabilities

## Impact

- **Database**: This is a complete rebuild of the underlying schema (approx. 36 tables).
- **ORM/JPA**: All existing entity classes will need to be rewritten or heavily modified.
- **Initial Data**: The application scaffolding/startup must be updated to load initial XML data mapped to the new schema instead of the legacy single-file import.
- **API/UI**: Any existing endpoints or storefront views relying on the old flat catalog or order structure will break and require updates to navigate the new entity graphs.
