### Requirement: Role-Based Access Control for Customers Module

The system SHALL restrict access to the customers module based on role assignment. A new "Customer Admin" role SHALL be available.

#### Scenario: Customer Admin access
- **WHEN** a user with the Customer Admin role logs in
- **THEN** they can access the customers module
- **AND** they cannot access the global dashboard or other modules

#### Scenario: Unauthorized access blocked
- **WHEN** a user without the customers module permission attempts to access `/backoffice/customers/**`
- **THEN** the `ModuleAccessInterceptor` blocks the request
- **AND** the Customers entry is hidden in the sidebar navigation
