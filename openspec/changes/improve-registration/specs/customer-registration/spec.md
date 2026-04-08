## ADDED Requirements

### Requirement: Password Complexity Validation
The system SHALL validate passwords against the established password complexity rules.

#### Scenario: User registers with valid password
- **WHEN** a user submits a registration form with a valid password
- **THEN** the system proceeds with registration

#### Scenario: User registers with weak password
- **WHEN** a user submits a registration form with a password that fails complexity requirements
- **THEN** the system rejects the registration and returns a localized validation error





### Requirement: Accessible and Localized UI
The registration form SHALL be fully accessible according to WCAG guidelines and support full internaliationalization (i18n) for labels and error messages.

#### Scenario: Screen reader navigation
- **WHEN** a user navigates the form with a screen reader
- **THEN** all form inputs, including validation messages, have appropriate ARIA attributes and labels

#### Scenario: Viewing in alternative language
- **WHEN** a user with a non-default locale views the registration page
- **THEN** all texts, including validation errors, are rendered in the selected locale

### Requirement: Email Verification Link
The system SHALL place new accounts into a pending state and send a confirmation email containing a magic link to activate the account.

#### Scenario: User clicks activation link
- **WHEN** a user clicks the valid activation link from their email
- **THEN** the system activates the account and logs the user in

#### Scenario: User clicks invalid or expired link
- **WHEN** a user clicks an activation link that is expired or malformed
- **THEN** the system displays an error page
- **AND** provides a button to resend a fresh verification email

#### Scenario: User attempts to log in before verification
- **WHEN** a user attempts to log in with correct credentials but their account is still pending
- **THEN** the system denies login and displays a message prompting them to check their email for the activation link

#### Scenario: User registers with an email already in use
- **WHEN** a user submits a registration form with an email that is already registered
- **THEN** the system gracefully redirects the user to the login page
- **AND** displays a friendly toast notification explaining they already have an account
- **AND** pre-fills the email field on the login form to save time

### Requirement: HTML5 Mobile Optimizations
The email input field SHALL use `type="email"` to ensure optimal mobile keyboard layout (revealing '@' and '.' characters automatically).

#### Scenario: User focuses email field on mobile
- **WHEN** a user focuses the email field on a mobile device
- **THEN** the device keyboard presents the optimized email entry layout

### Requirement: Instant Inline Format Validation
The system SHALL validate input fields continuously and provide instant visual feedback on format as the user types or leaves a field, strictly without querying the database for user existence to prevent enumeration.

#### Scenario: Typing an invalid or missing required field
- **WHEN** a user types invalid data or blurs an empty required field
- **THEN** the system immediately shows localized validation error messages below the field without a full page reload

#### Scenario: Screen reader announces inline validation
- **WHEN** an inline validation error is displayed via HTMX
- **THEN** the error container uses an `aria-live` attribute to immediately announce the error to screen reader users

### Requirement: Password Visibility Toggle
The system SHALL allow users to toggle the visibility of the password fields using an eye-icon to prevent typos, avoiding the need for redundant double-entry fields.

#### Scenario: Toggling password visibility
- **WHEN** a user clicks the eye icon on the password field
- **THEN** the field type switches from `password` to `text` and vice versa, exposing or masking the characters

#### Scenario: Screen reader announces toggle state
- **WHEN** the password visibility is toggled
- **THEN** the eye-icon updates its `aria-pressed` state and `aria-label` to reflect the current visibility status

### Requirement: Alternative Navigation Paths
The registration page SHALL provide prominent navigation links to the login and password reset flows to assist returning users.

#### Scenario: User navigates to Login
- **WHEN** a user clicks the "Already a member? Log in" link
- **THEN** the system directs them to the standard login screen

#### Scenario: User navigates to Password Reset
- **WHEN** a user clicks the "Forgot Password?" link
- **THEN** the system directs them to the password reset flow
