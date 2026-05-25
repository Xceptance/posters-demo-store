## Why

The current storefront "Create Account" (registration) form lacks a modern, unified design, causing inconsistencies with other premium user-facing pages such as the checkout address cards. Furthermore, a critical bug exists in localized environments where translating the "Already have an account?" text replaces the entire container content, rendering the login redirection link completely missing/inactive.

## What Changes

- **Centered Card Layout**: Re-style the registration container as a horizontally centered, white card-style container with subtle borders and shadows (`card shadow-sm border-0`).
- **Right-Aligned Submission Button**: Position the "Create Account" submission button on the right side of the bottom row.
- **Active Redirection Link**: Keep the "Already have an account? Login" redirection link active by structuring the Thymeleaf translation placeholder on a dedicated child `<span>` element rather than overwriting the entire container `<p>` block.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `storefront`: Add requirements for the storefront "Create Account" (registration) form visual layout, button alignment, and active localized redirection links.

## Impact

- **Templates**: `customer/register.html` (form layout structure, button classes, and translation text bindings).
- **Localization**: No modifications to the properties files are needed, but HTML structural isolation is required to prevent the existing translations from breaking the login link.
