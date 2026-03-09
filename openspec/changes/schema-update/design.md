## Context

The `posters-demo-store` currently relies on a legacy, flat single-file data import that maps to an overly simplified entity model. As the project evolves to support more advanced SaaS and multi-tenant e-commerce features, the underlying database schema needs to be completely replaced. The new model, detailed in `doc/rework/entity-model-spec.md`, introduces a sophisticated 3-tier product hierarchy, explicit multi-region pricing/tax support, and immutable order snapshots.

## Goals / Non-Goals

**Goals:**
- Completely normalize the product catalog into `products`, `variants`, and localized attributes.
- Support multi-region configurations via a root `sites` table linking to dedicated locale, currency, tax, and price tables.
- Ensure strict immutability for all historical data by explicitly snapshotting customers, addresses, credit cards, and prices into `order_*` tables during checkout.
- Provide a clear data migration path by replacing the single-file import with a set of XML files that map precisely to the new relational structure for initial setup.

**Non-Goals:**
- **Tiered Volume Pricing:** While the schema separates prices, we are not implementing quantity-based price breaks at this time.
- **Promotional Pricing/MSRP:** Storing "Compare At" prices or discount logic is deferred to a future iteration.
- **Site-Specific Product Visibility:** All products will remain globally addressable; we are not introducing a `site_products` restriction table yet.
- **Automated Live Data Migration:** We are only concerned with migrating the initial setup/import data, not writing complex SQL scripts to migrate user data on a live production instance.

## Decisions

1. **3-Tier Product Model (`Product` -> `Variant` -> `SKU`)**
   - *Rationale:* Decouples master marketing data from purchasable units. Prices and inventory are only attached to SKUs, reducing duplication and preventing cart errors.
2. **Immutable Orders via Snapshots**
   - *Rationale:* E-commerce systems must guarantee that if a product's price or a user's name changes tomorrow, yesterday's order remains perfectly intact for auditing and compliance.
3. **Decoupled Cart Pricing**
   - *Rationale:* Carts store the SKU and quantity but NOT the price. Prices are fetched live during display. This prevents users from locking in old prices by keeping items in a stale cart.
4. **Global Variation Attributes**
   - *Rationale:* Instead of defining "Color" independently on every product, we use global `variation_attributes` joined to products. This allows for clean, site-wide category filtering by attribute values.

## Risks / Trade-offs

- **[Risk] High Implementation Effort:** This is a rewrite of over 30 tables and the corresponding JPA entities.
  - *Mitigation:* We will rely strictly on the `doc/rework/entity-model-spec.md` as the single source of truth. The work should be split into distinct task phases (Catalog, Users, Checkout) rather than a single monolithic PR.
- **[Risk] Broken Frontend/API Contracts:** The storefront and backoffice APIs currently expect the flat model.
  - *Mitigation:* We must update the DTOs and API controllers concurrently with the database changes to ensure the UI can still render products and process checkouts.

## Migration Plan

1. Generate the new JPA entity classes based on `doc/rework/entity-model-spec.md`.
2. Generate the corresponding Liquibase/Flyway schema management scripts.
3. Update the initial data load mechanism to parse a new set of XML files mapping to this schema, abandoning the old single flat-file approach.
