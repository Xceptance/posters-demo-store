## ADDED Requirements

### Requirement: Registration Form Card-Style Visual Layout
The system SHALL present the storefront "Create Account" (registration) form as a horizontally centered, white card-style container with subtle borders and shadows to ensure layout parity with checkout forms.

#### Scenario: Verify registration page layout is centered card
- **WHEN** the user visits the registration page (`/{locale}/register`)
- **THEN** the registration form is wrapped in a card container that is horizontally centered on the page

### Requirement: Registration Submission Button Alignment
The registration form SHALL position the "Create Account" submission button on the right-hand side of the bottom actions row.

#### Scenario: Verify register button is on the right
- **WHEN** the user is on the registration page (`/{locale}/register`)
- **THEN** the "Create Account" submit button is positioned on the right-hand side of the actions row

### Requirement: Active Localized Login Redirection Link
The registration form SHALL maintain an active, clickable login redirection link (pointing to `/{locale}/login`) across all supported locales, ensuring that localized texts do not overwrite the anchor tag.

#### Scenario: Verify redirection link is active under Swedish locale
- **WHEN** the user is on the Swedish registration page (`/sv/register`)
- **THEN** the link labeled "Logga in" is visible, active, and redirects the user to `/sv/login` when clicked
