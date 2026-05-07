## ADDED Requirements

### Requirement: Customer Detail View

The system SHALL provide a read-only profile summary page for a specific customer.

#### Scenario: View customer details
- **WHEN** an admin clicks on a customer from the list
- **THEN** they see a detail view showing customer number, name, email, order count, last login, and created date
- **AND** sections for addresses and masked credit cards are displayed

#### Scenario: Audit logging of view action
- **WHEN** a customer detail page is viewed
- **THEN** a `CUSTOMER_VIEWED` event is recorded in the audit log
