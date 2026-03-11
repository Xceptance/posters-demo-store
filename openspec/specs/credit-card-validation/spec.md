# Credit Card Validation

## Purpose

Defines the validation rules for credit card payment processing, including vendor detection from BIN prefixes, Luhn checksum, vendor-specific length, expiry date, CVV length, server-side mirroring of client-side rules, and PAN masking for display.

## Requirements

### Requirement: Vendor detection from BIN prefix
The system SHALL detect the card vendor from the card number's BIN (Bank Identification Number) prefix. Supported vendors: Visa, Mastercard, Amex, UnionPay, JCB, Discover, Diners Club, and Maestro. Detection SHALL work identically on client-side (JavaScript) and server-side (Java) using a shared BIN prefix table.

#### Scenario: Known vendor detected
- **WHEN** the user enters a card number starting with a recognized BIN prefix (e.g., `4` for Visa, `51`–`55` for Mastercard, `34`/`37` for Amex)
- **THEN** the system SHALL identify the correct vendor

#### Scenario: Unknown prefix
- **WHEN** the entered digits do not match any known vendor's BIN prefix
- **THEN** the system SHALL return a neutral/unknown vendor state (not an error)

#### Scenario: Prefix changes as digits are entered
- **WHEN** the user types additional digits that change the matched vendor (e.g., `3` could be Amex or Diners; `34` resolves to Amex)
- **THEN** the system SHALL update the detected vendor in real time

### Requirement: Luhn checksum validation
The system SHALL validate the card number using the Luhn algorithm (mod-10 checksum). Validation SHALL be performed both client-side (on blur and on submit) and server-side.

#### Scenario: Valid Luhn checksum
- **WHEN** a card number passes the Luhn checksum
- **THEN** the system SHALL accept the number as structurally valid

#### Scenario: Invalid Luhn checksum
- **WHEN** a card number fails the Luhn checksum
- **THEN** the system SHALL reject the number with an inline error message

### Requirement: Vendor-specific card number length validation
The system SHALL enforce valid card number lengths per vendor (e.g., 16 for Visa/Mastercard, 15 for Amex, 14 for Diners Club). The system SHALL support vendor-specific length ranges where applicable.

#### Scenario: Correct length for detected vendor
- **WHEN** a card number has a valid length for its detected vendor
- **THEN** the system SHALL accept the length as valid

#### Scenario: Incorrect length for detected vendor
- **WHEN** a card number has an invalid length for its detected vendor
- **THEN** the system SHALL display an inline error indicating the expected length

### Requirement: Expiry date validation
The system SHALL validate the combined MM/YY expiry input. The month SHALL be between 01 and 12. The expiry date SHALL not be in the past. The maximum accepted year SHALL be the current year plus 20.

#### Scenario: Valid future expiry
- **WHEN** the user enters an expiry date that is in the current month or a future month/year within 20 years
- **THEN** the system SHALL accept the expiry as valid

#### Scenario: Expired date
- **WHEN** the user enters an expiry date in the past
- **THEN** the system SHALL display an inline error indicating the card has expired

#### Scenario: Invalid month value
- **WHEN** the user enters a month value outside the range 01–12
- **THEN** the system SHALL display an inline error indicating an invalid month

#### Scenario: Expiry too far in the future
- **WHEN** the user enters a year more than 20 years from the current year
- **THEN** the system SHALL display an inline error indicating an invalid expiry year

### Requirement: CVV length validation
The system SHALL validate the CVV/CVC length based on the detected vendor: 4 digits for Amex, 3 digits for all other vendors. CVV SHALL be validated but never stored in the database.

#### Scenario: Correct CVV length
- **WHEN** the CVV length matches the expected length for the detected vendor
- **THEN** the system SHALL accept the CVV as valid

#### Scenario: Incorrect CVV length
- **WHEN** the CVV length does not match the expected length for the detected vendor
- **THEN** the system SHALL display an inline error indicating the expected CVV length

### Requirement: Server-side validation mirrors client-side
The system SHALL enforce all validation rules (Luhn, vendor-specific length, expiry range, CVV length) on the server side via a `CreditCardValidator` service class. Server-side validation SHALL reject the form submission with appropriate error messages if any rule fails, regardless of whether client-side validation was bypassed.

#### Scenario: Client-side validation bypassed
- **WHEN** a form is submitted with invalid data and client-side JavaScript is disabled
- **THEN** the server SHALL reject the submission and return validation errors

#### Scenario: Server-side re-renders form with errors
- **WHEN** server-side validation fails
- **THEN** the system SHALL re-render the payment form with inline error messages for each failing field

### Requirement: PAN masking for display
The system SHALL mask card numbers using the standard PAN format — first 6 and last 4 digits visible, middle digits replaced with asterisks (e.g., `512345******3456`). Masking SHALL be applied via a shared `CreditCardMasker` utility whenever card numbers are re-displayed.

#### Scenario: Masked display after validation error
- **WHEN** the payment form is re-rendered after a server-side validation error
- **THEN** the card number field SHALL display the masked PAN format

#### Scenario: Masked display on order review
- **WHEN** the user reaches the order review page
- **THEN** the card number SHALL be displayed in the masked PAN format

#### Scenario: Masked display on order confirmation
- **WHEN** the user reaches the order confirmation page
- **THEN** the card number SHALL be displayed in the masked PAN format

#### Scenario: CVV never re-displayed
- **WHEN** the payment form is re-rendered or the card is displayed on any page
- **THEN** the CVV SHALL never be included in the output
