# backoffice-users

## Purpose

Defines the admin user authentication system for the backoffice. Admin users are stored in a separate `admin_users` table, completely independent from storefront customers. Includes login/logout flows, BCrypt password hashing, and Spring Security integration with dual filter chains.

## Requirements

### Requirement: Admin Users MUST Be Independent from Storefront Customers

The system SHALL maintain a separate `admin_users` table for backoffice authentication. Admin accounts MUST NOT be stored in or validated against the `customers` table. The two user pools SHALL have no shared authentication logic.

#### Scenario: Admin credentials do not work on storefront

- **WHEN** a user attempts to log into the storefront using admin credentials
- **THEN** the login attempt fails
- **AND** no cross-authentication occurs between admin and customer user pools

#### Scenario: Customer credentials do not work in backoffice

- **WHEN** a user attempts to log into the backoffice using storefront customer credentials
- **THEN** the login attempt fails with "Invalid username or password"

### Requirement: Admin User Entity SHALL Store Credentials Securely

The `AdminUser` entity SHALL have the following fields: `id` (Long, auto-increment), `username` (unique, not null), `password` (BCrypt-hashed, not null), `displayName` (not null), `createdAt` (timestamp, set on creation).

#### Scenario: Admin user record structure

- **WHEN** inspecting the `admin_users` table
- **THEN** it contains columns for `id`, `username`, `password`, `display_name`, and `created_at`
- **AND** `username` has a unique constraint
- **AND** `password` stores a BCrypt hash (starts with `$2a$` or `$2b$`)

### Requirement: Default Admin Account SHALL Be Seeded at Startup

The system SHALL seed a default admin account on startup if no admin users exist. The default account SHALL use username `admin` and password `admin-2026!` (BCrypt-hashed). The seeding MUST be idempotent — it SHALL NOT create duplicates on subsequent startups.

#### Scenario: First startup seeds admin account

- **WHEN** the application starts for the first time
- **AND** the `admin_users` table is empty
- **THEN** a default admin account is created with username `admin` and display name `Administrator`
- **AND** the password is BCrypt-hashed

#### Scenario: Subsequent startup does not duplicate admin

- **WHEN** the application starts again
- **AND** the `admin_users` table already contains one or more users
- **THEN** no additional admin accounts are created

### Requirement: Backoffice Login Page SHALL Authenticate Admin Users

The backoffice SHALL provide a login page at `/backoffice/login`. The login form SHALL accept `username` and `password` fields and validate them against the `admin_users` table using BCrypt comparison.

#### Scenario: Successful login

- **WHEN** user navigates to `/backoffice/login`
- **AND** submits valid admin credentials (username `admin`, password `admin-2026!`)
- **THEN** the user is redirected to `/backoffice/`
- **AND** an authenticated session is established

#### Scenario: Failed login with wrong password

- **WHEN** user submits an incorrect password on the login page
- **THEN** the login page redisplays with an error message
- **AND** no session is created

#### Scenario: Failed login with unknown username

- **WHEN** user submits a username that does not exist in `admin_users`
- **THEN** the login page redisplays with an error message
- **AND** the error message does not reveal whether the username or password was wrong

### Requirement: Backoffice Logout SHALL End the Admin Session

The backoffice SHALL provide a logout mechanism at `/backoffice/logout` (POST). After logout, the user SHALL be redirected to the login page.

#### Scenario: Successful logout

- **WHEN** an authenticated admin user triggers logout
- **THEN** the admin session is invalidated
- **AND** the user is redirected to `/backoffice/login?logout`

#### Scenario: Accessing backoffice after logout

- **WHEN** a user has logged out
- **AND** attempts to access `/backoffice/`
- **THEN** the user is redirected to `/backoffice/login`

### Requirement: Spring Security SHALL Protect Backoffice URLs

The system SHALL use Spring Security with two `SecurityFilterChain` beans. The backoffice chain SHALL require authentication for all `/backoffice/**` URLs except the login page. The storefront chain SHALL permit all requests without authentication.

#### Scenario: Unauthenticated access to backoffice redirects to login

- **WHEN** an unauthenticated user navigates to `/backoffice/`
- **THEN** the user is redirected to `/backoffice/login`

#### Scenario: Storefront remains fully accessible without auth

- **WHEN** any user navigates to a storefront URL (e.g., `/`, `/en/products`)
- **THEN** the page loads without any login prompt or redirect
- **AND** Spring Security does not interfere with storefront behavior

#### Scenario: CSRF does not block storefront forms

- **WHEN** a storefront user submits a form (e.g., add to cart, checkout)
- **THEN** the form submission succeeds without CSRF token errors
