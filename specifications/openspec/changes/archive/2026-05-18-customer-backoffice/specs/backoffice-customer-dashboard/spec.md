## ADDED Requirements

### Requirement: Customer Module Dashboard

The customer module SHALL have its own dashboard accessible to the Customer Admin role.

#### Scenario: View customer dashboard tiles
- **WHEN** a Customer Admin accesses `/backoffice/customers/dashboard`
- **THEN** they see tiles for total customer count, customers created in the last 24h, and customers with new orders in the last 24h
- **AND** an EChart visualizing customer creation over the last 24h
