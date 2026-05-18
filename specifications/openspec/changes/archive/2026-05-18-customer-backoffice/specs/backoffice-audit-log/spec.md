## MODIFIED Requirements

### Requirement: Audit Log SHALL Record All Significant User Activities

The system SHALL record an audit log entry for every significant administrative action performed by a user. Each entry SHALL include: `id`, `timestamp`, `userId`, `username`, `action` (e.g., `USER_CREATED`, `ROLE_ASSIGNED`, `LOGIN`, `LOGOUT`, `CUSTOMER_VIEWED`, `CUSTOMER_UPDATED`, `CUSTOMER_ADDRESS_CREATED`, `CUSTOMER_ADDRESS_UPDATED`, `CUSTOMER_ADDRESS_DELETED`, `CUSTOMER_CARD_ADDED`, `CUSTOMER_CARD_DELETED`), `targetType` (e.g., `AdminUser`, `Role`, `Customer`), `targetId`, and `details` (free-text description of what changed).

#### Scenario: User creation is logged

- **WHEN** an admin creates a new user
- **THEN** an audit log entry is created with action `USER_CREATED`
- **AND** the entry includes the admin's user ID, the new user's ID as target, and creation details

#### Scenario: Login is logged

- **WHEN** a user successfully logs in
- **THEN** an audit log entry is created with action `LOGIN`
- **AND** the entry includes the user's ID and timestamp

#### Scenario: Role assignment is logged

- **WHEN** an admin assigns a role to a user
- **THEN** an audit log entry is created with action `ROLE_ASSIGNED`
- **AND** the entry includes which role was assigned to which user

#### Scenario: Customer actions are logged

- **WHEN** an admin performs an action on a customer (e.g., views, updates)
- **THEN** an audit log entry is created with the corresponding action type
