## 1. Backend Validation & Security Layer

- [x] 1.1 Implement password complexity enforcement rules in backend validator
- [x] 1.2 Add email verification tokens and implement pending account states
- [x] 1.3 Add `account_status`, `verification_token`, and `token_expires_at` columns to the Customer database schema.

## 2. Controllers

- [x] 2.1 Create activation endpoint that accepts magic links and completes registration
- [x] 2.2 Create an endpoint to resend activation links for accounts that are stuck in a pending state
- [x] 2.3 Ensure detailed and localized validation messages are returned to the model upon failure
- [x] 2.4 Create format-only HTMX endpoint handles for inline field validation (do NOT query DB for existing users)
- [x] 2.5 Handle POST registration for existing emails by gracefully redirecting to the login page with pre-filled email flash attributes
- [x] 2.6 Trim whitespace from email fields to prevent trailing space bugs before format validation or lookup

## 3. Frontend & Layout

- [x] 3.1 Improve form markup for WCAG accessibility (add proper `for`, `aria-label`, and `aria-describedby` attributes)
- [x] 3.2 Set Email input `type="email"` for proper mobile keyboard layout
- [x] 3.3 Ensure validation error messages use `aria-live` so screen readers announce HTMX updates instantly
- [x] 3.4 Implement a client-side toggle (eye icon) to switch the password field visibility
- [x] 3.5 Bind dynamic `aria-pressed` and `aria-label` states to the password eye-icon toggle
- [x] 3.6 Add `hx-post` and `hx-trigger="blur, keyup changed delay:500ms"` attributes to input fields to wire up instant feedback
- [x] 3.7 Add a prominent "Already a member? Log in" navigation link below the registration form
- [x] 3.8 Add a prominent "Forgot Password?" navigation link below the registration form
- [x] 3.9 Add `autocomplete="email"` and `autocomplete="new-password"` attributes to the input fields for password manager support
- [x] 3.10 Create HTML/Text Thymeleaf templates for the outbound Verification Link and Account Exists emails

## 4. Testing

- [x] 4.1 Write unit tests for password complexity logic
- [x] 4.2 Write integration tests simulating full registration flows, including activation loop and format validation
