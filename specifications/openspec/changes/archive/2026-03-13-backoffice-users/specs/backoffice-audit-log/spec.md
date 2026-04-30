## ADDED Requirements

### Requirement: Audit Log SHALL Record All Significant User Activities

The system SHALL record an audit log entry for every significant administrative action performed by a user. Each entry SHALL include: `id`, `timestamp`, `userId`, `username`, `action` (e.g., `USER_CREATED`, `ROLE_ASSIGNED`, `LOGIN`, `LOGOUT`), `targetType` (e.g., `AdminUser`, `Role`), `targetId`, and `details` (free-text description of what changed).

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

### Requirement: Audit Log SHALL Be Searchable and Paginated

The Admin > Audit Log submodule SHALL display all audit log entries in a paginated table. The table SHALL support searching by username, action type, and free-text details. Results SHALL be sorted by timestamp descending (most recent first).

#### Scenario: Audit log displays recent entries

- **WHEN** an admin navigates to Admin > Audit Log
- **THEN** a paginated table shows the most recent audit entries
- **AND** each row displays timestamp, username, action, target, and details

#### Scenario: Search by username

- **WHEN** an admin enters a username in the search field
- **THEN** the table filters to show only entries for that user

#### Scenario: Search by action type

- **WHEN** an admin selects an action type filter (e.g., `USER_CREATED`)
- **THEN** the table filters to show only entries with that action

### Requirement: Audit Log Entries SHALL Be Retained for 180 Days

The system SHALL automatically remove audit log entries older than 180 days. Cleanup SHALL run on a scheduled basis (e.g., daily). Entries within the retention period SHALL NOT be deletable via the UI.

#### Scenario: Entries older than 180 days are removed

- **WHEN** the scheduled cleanup runs
- **THEN** all audit log entries with a timestamp older than 180 days are deleted
- **AND** entries within the retention period are not affected

#### Scenario: No manual deletion of audit entries

- **WHEN** an admin views the audit log
- **THEN** there are no delete buttons or actions available for individual entries
