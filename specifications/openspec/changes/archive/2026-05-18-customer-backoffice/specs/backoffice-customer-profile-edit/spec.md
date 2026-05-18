## ADDED Requirements

### Requirement: Customer Profile Editing

The system SHALL allow admins to edit specific fields of a customer's profile.

#### Scenario: Edit name fields
- **WHEN** an admin edits a customer profile
- **THEN** they can modify the first name, last name, and middle name
- **AND** email and password remain uneditable

#### Scenario: Audit logging of profile edits
- **WHEN** a customer profile is updated
- **THEN** a `CUSTOMER_UPDATED` event is recorded in the audit log
