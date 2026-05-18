## ADDED Requirements

### Requirement: Customer Address Management

The system SHALL allow admins to view, add, edit, and delete customer addresses.

#### Scenario: Manage addresses
- **WHEN** an admin interacts with the addresses section on the detail view
- **THEN** they can add a new address with a user-defined name
- **AND** they can edit or delete existing addresses with confirmation

#### Scenario: Audit logging of address changes
- **WHEN** an address is created, updated, or deleted
- **THEN** the corresponding `CUSTOMER_ADDRESS_*` event is recorded in the audit log
