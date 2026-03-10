# Catalog Management

## ADDED Requirements

### Requirement: 3-Tier Product Hierarchy
The system MUST support a 3-tier product catalog consisting of Master Products, Variants, and Purchasable SKUs.

#### Scenario: Navigating the Hierarchy
Given a Master Product exists in the catalog
When a customer views the product details
Then they can select different Variants (e.g., Color, Size)
And each selection resolves to a specific Purchasable SKU.

### Requirement: Global Variation Attributes
The system MUST define variation attributes (e.g., "Color", "Size") globally, allowing multiple products to share the same attribute definitions.

#### Scenario: Assigning Attributes
Given a global "Color" attribute exists
When an administrator creates a new Master Product
Then they can link the "Color" attribute to the product without redefining it.

### Requirement: Flat Category Structure
The catalog category tree MUST be limited to a maximum depth of two levels (Top Category -> Sub Category).

#### Scenario: Category Navigation
Given a Top Category "Clothing" with a Sub Category "Shirts"
When an administrator attempts to add a child category to "Shirts"
Then the system rejects the operation, enforcing the 2-level limit.
