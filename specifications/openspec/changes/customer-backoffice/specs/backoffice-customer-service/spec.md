## ADDED Requirements

### Requirement: Customer Service Layer

The system SHALL implement a dedicated service layer for customer backoffice logic.

#### Scenario: Retrieve customer data
- **WHEN** backoffice customer operations are performed
- **THEN** they use the new `CustomerService` and `CustomerRepository` queries
- **AND** do not reuse storefront controllers or services
