## 1. Foundation: Site & Localization

- [x] 1.1 TDD: Create `Locale` and `LocalizedText` entities and DTOs (write tests first)
- [x] 1.2 TDD: Create `Site` entity (with currency and locale mappings)
- [x] 1.3 TDD: Implement `SiteRepository` and initialization logic
- [x] 1.4 Write integration tests for Site configuration loading

## 2. Core Catalog

- [x] 2.1 TDD: Create 2-level `Category` entity with self-referential parent mapping
- [x] 2.2 TDD: Create `VariationAttribute` and `VariationAttributeValue` entities
- [x] 2.3 TDD: Create `Product` (master) entity
- [x] 2.4 TDD: Create `Variant` entity and link to Product and Attributes
- [x] 2.5 TDD: Ensure SKU constraint logic (`[A-Z0-9]{6,10}-[0-9]{4}`) is enforced via tests
- [x] 2.6 Write integration tests for 3-tier product catalog navigation

## 3. Pricing, Taxes & Inventory

- [x] 3.1 TDD: Create `PriceTable` and `Price` entities
- [x] 3.2 TDD: Create `TaxTable` and `TaxRate` entities
- [x] 3.3 TDD: Create `InventoryTable` and `InventoryEntry` entities
- [x] 3.4 TDD: Wire Site entity to Price, Tax, and Inventory tables
- [x] 3.5 Write integration tests for fetching price bounds and availability

## 4. Users & Authentication

- [x] 4.1 TDD: Create `Customer` and `CustomerProfile` entities
- [x] 4.2 TDD: Create `Address` and `CreditCard` entities
- [x] 4.3 TDD: Implement `Session` tracking (anonymous vs authenticated)
- [x] 4.4 Write integration tests for customer login and session merging

## 5. Checkout & Orders

- [x] 5.1 TDD: Create `ShippingMethod` and `SiteShippingMethod` mapping
- [x] 5.2 TDD: Create temporary `Cart`, `CartLineItem`, and Cart address/payment entities
- [x] 5.3 TDD: Create immutable `Order`, `OrderLineItem`, and Order address/payment snapshot entities
- [x] 5.4 TDD: Implement cart-to-order snapshot conversion logic
- [x] 5.5 Write integration tests for checkout flow and order immutability

## 6. Data Migration

- [x] 6.1 TDD: Define XML structure for initial setup data mapping to the new schema
- [x] 6.2 TDD: Write XML parser for the new structure (with tests)
- [x] 6.3 TDD: Implement data loader service to populate DB on startup
      - **REQUIREMENT**: Ensure clear INFO-level output during import progress and explicitly formatted error messages for bad/missing data.
- [x] 6.4 Write integration tests parsing XML, validating inserted records exist, and verifying correct error logging

## 7. API / UI Updates

- [x] 7.1 TDD: Update catalog API/controllers to traverse new Category/Product/Variant model
- [x] 7.2 TDD: Update checkout API/controllers to use new Cart -> Order snapshot flow
- [x] 7.3 TDD: Update UI templates/Javascript and corresponding integration tests to handle new API structures
