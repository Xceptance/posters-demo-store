## ADDED Requirements

### Requirement: External Customer Number

The `Customer` entity SHALL be assigned an external, auto-incrementing customer number upon creation.

#### Scenario: Customer number assignment
- **WHEN** a new customer is created
- **THEN** an auto-incrementing, zero-padded customer number (e.g., `0000001`) is assigned
- **AND** it is displayed in the backoffice UI where human-readable identifiers are needed
