### Requirement: Customer Credit Card Lifecycle

The system SHALL allow admins to add and delete credit cards for a customer, with strict masking and validation rules.

#### Scenario: Add a credit card
- **WHEN** an admin adds a credit card
- **THEN** the form validates card vendor, auto-formats, and checks Luhn validation
- **AND** CVV is not collected

#### Scenario: Credit card masking
- **WHEN** a credit card is saved and viewed
- **THEN** only the masked card number is stored and displayed

#### Scenario: Audit logging of credit card changes
- **WHEN** a credit card is added or deleted
- **THEN** the corresponding `CUSTOMER_CARD_*` event is recorded in the audit log
