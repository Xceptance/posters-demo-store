## ADDED Requirements

### Requirement: Dynamic vendor logo display
The system SHALL display the detected card vendor's logo/icon dynamically as the user types the card number. When no vendor is detected, the system SHALL show a neutral/generic state with no vendor icon highlighted.

#### Scenario: Vendor logo appears on typing
- **WHEN** the user types digits that match a known vendor BIN prefix
- **THEN** the corresponding vendor logo/icon SHALL be displayed with a smooth transition animation

#### Scenario: Vendor logo updates on prefix change
- **WHEN** additional digits change the detected vendor
- **THEN** the displayed vendor logo SHALL transition to the new vendor's logo

#### Scenario: No vendor matched
- **WHEN** typed digits do not match any known vendor
- **THEN** the system SHALL display a neutral/generic card icon (not an error state)

### Requirement: Card number auto-formatting
The system SHALL auto-format the card number with visual spacing in groups as the user types: 4-4-4-4 for most vendors, 4-6-5 for Amex, 4-6-4 for 14-digit Diners Club. Space characters SHALL be inserted for display; digits only SHALL be submitted to the server.

#### Scenario: Standard formatting while typing
- **WHEN** the user types a Visa/Mastercard/JCB/Discover/UnionPay card number
- **THEN** digits SHALL be grouped as 4-4-4-4 with spaces between groups

#### Scenario: Amex formatting
- **WHEN** the user types an Amex card number (prefix `34` or `37`)
- **THEN** digits SHALL be grouped as 4-6-5 with spaces between groups

#### Scenario: Diners Club formatting
- **WHEN** the user types a 14-digit Diners Club card number
- **THEN** digits SHALL be grouped as 4-6-4 with spaces between groups

#### Scenario: Copy-paste handling
- **WHEN** the user pastes a card number containing spaces, dashes, or other non-digit characters
- **THEN** the system SHALL strip all non-digit characters and apply the correct formatting

#### Scenario: Digits stripped before submission
- **WHEN** the form is submitted
- **THEN** only digits (no spaces) SHALL be sent in the card number form parameter

### Requirement: Combined MM/YY expiry input
The system SHALL provide a single text input for card expiry with `placeholder="MM/YY"`, `autocomplete="cc-exp"`, `inputmode="numeric"`, and `maxlength="5"`. The system SHALL auto-insert a `/` separator after the second digit.

#### Scenario: Auto-slash insertion
- **WHEN** the user types two digits for the month
- **THEN** a `/` character SHALL be automatically inserted after the second digit

#### Scenario: Parsed into month and year on submit
- **WHEN** the form is submitted with a valid `MM/YY` value
- **THEN** the controller SHALL parse it into separate month and year integers for the entity model

#### Scenario: Backspace through the slash
- **WHEN** the user presses backspace with the cursor after the `/`
- **THEN** the `/` and the preceding digit SHALL be deleted, leaving one digit

### Requirement: CVV input field
The system SHALL provide a CVV/CVC input field with `inputmode="numeric"` and vendor-aware `maxlength` (4 for Amex, 3 for all others). CVV SHALL be accepted for validation purposes but never persisted to the database.

#### Scenario: Amex CVV length
- **WHEN** the detected vendor is Amex
- **THEN** the CVV field SHALL accept up to 4 digits

#### Scenario: Non-Amex CVV length
- **WHEN** the detected vendor is not Amex (or unknown)
- **THEN** the CVV field SHALL accept up to 3 digits

### Requirement: Auto-tab field progression
The system SHALL automatically advance focus from card number → expiry → CVV as each field reaches its expected maximum length.

#### Scenario: Card number complete
- **WHEN** the user enters the maximum number of digits for the detected vendor
- **THEN** focus SHALL automatically move to the expiry field

#### Scenario: Expiry complete
- **WHEN** the user enters `MM/YY` (5 characters including the slash)
- **THEN** focus SHALL automatically move to the CVV field

### Requirement: Inline validation feedback
The system SHALL display inline error messages below each field when validation fails. Errors SHALL appear on blur and on submit. Errors SHALL clear on blur when the field value is corrected.

#### Scenario: Error on blur
- **WHEN** the user leaves a field (blur event) with an invalid value
- **THEN** an inline error message SHALL appear below that field

#### Scenario: Error clears on correction
- **WHEN** the user corrects a field and blurs away
- **THEN** the previously displayed error message SHALL be removed

#### Scenario: All errors shown on submit
- **WHEN** the user submits the form with multiple invalid fields
- **THEN** inline error messages SHALL appear for all failing fields simultaneously

### Requirement: Browser autocomplete support
The system SHALL use standard `autocomplete` attributes on payment fields: `cc-number` for card number, `cc-name` for cardholder name, `cc-csc` for CVV, and `cc-exp` for the combined expiry field.

#### Scenario: Browser/password manager autofill
- **WHEN** the user's browser or password manager offers to autofill payment information
- **THEN** the correct fields SHALL be populated based on `autocomplete` attribute matching

### Requirement: Mobile numeric keyboard
The system SHALL use `inputmode="numeric"` on the card number, CVV, and expiry fields to trigger the numeric keyboard on mobile devices.

#### Scenario: Numeric keyboard on mobile
- **WHEN** a user taps the card number, CVV, or expiry field on a mobile device
- **THEN** the device SHALL display the numeric keyboard

### Requirement: Accessibility
The system SHALL provide ARIA labels on all payment fields, manage focus after validation errors (focus moves to the first invalid field), announce vendor detection changes via ARIA live regions for screen readers, and support full keyboard-only navigation.

#### Scenario: Screen reader vendor announcement
- **WHEN** the detected vendor changes during card number entry
- **THEN** the change SHALL be announced via an ARIA live region

#### Scenario: Focus management after validation error
- **WHEN** the form submission fails validation
- **THEN** focus SHALL move to the first field with an error

#### Scenario: Keyboard-only navigation
- **WHEN** a user navigates the payment form using only the keyboard
- **THEN** all fields, buttons, and error messages SHALL be reachable via Tab/Shift-Tab and operable via Enter/Space

### Requirement: Back-navigation yields fresh form
The system SHALL present a fresh, empty payment form when the user navigates back to the payment page from the order review page. State restoration is not supported in this change.

#### Scenario: Navigate back from order review
- **WHEN** the user navigates back to the payment page from the order review page
- **THEN** all payment fields SHALL be empty (no pre-populated data)

### Requirement: Luxury UI styling
The payment form SHALL feature premium styling consistent with the application's design system: subtle animations, vendor logo transitions, floating labels, a dark-accent card preview feel, and a visual "Secure Payment" trust badge with a lock icon.

#### Scenario: Premium visual appearance
- **WHEN** the payment page loads
- **THEN** the form SHALL display with premium styling including floating labels, smooth transitions, and a trust badge

#### Scenario: Trust badge visibility
- **WHEN** the payment page is displayed
- **THEN** a "Secure Payment" trust badge with a lock icon SHALL be visible

### Requirement: HTMX inline server-side validation (optional)
The system MAY use HTMX to call a server-side validation endpoint on blur/change for individual fields, providing unified validation feedback without a full page reload.

#### Scenario: HTMX field validation on blur
- **WHEN** the user blurs a payment field and HTMX validation is enabled
- **THEN** the system SHALL send the field value to the server and display any returned validation error inline

### Requirement: Test card documentation
The system SHALL provide a reference list of valid test card numbers per vendor for demo and testing purposes.

#### Scenario: Test cards available
- **WHEN** a developer or tester needs to test the payment form
- **THEN** a documented list of test card numbers (one per supported vendor, passing Luhn validation) SHALL be available
