### Requirement: Customer Login and Password Tracking

The system SHALL track the last login time and initialize the last password change time.

#### Scenario: Update last login on successful login
- **WHEN** a customer successfully authenticates in the storefront
- **THEN** their `lastLogin` timestamp is updated

#### Scenario: Initialize last password change
- **WHEN** a new customer profile is created
- **THEN** the `lastPasswordChange` timestamp is initialized to the `createdAt` timestamp
