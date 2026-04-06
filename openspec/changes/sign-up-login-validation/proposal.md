# Registration Validation Proposal

## Why

The account registration flow currently offers near-zero client-side feedback: email is
only checked at format level by the browser's built-in `type="email"` attribute, and
password quality is enforced by a single `minlength="6"` HTML attribute that any
half-decent password trivially satisfies. Users get no guidance on what makes a strong
password, and duplicate-email errors are only discovered after a full round-trip to the
server. Better upfront validation improves the user experience and reduces wasted server
requests while meeting baseline security requirements.

## User Stories

### Registration

* I am a new user
* When I type my password, I want to see if this fitting the basic requirements
* When I type my email, I want to see instantly that this email is legit (even though it might be wrong, but the format is right)
* When I mistype the second password I want to see that instantly
* I want to be able to make my password visible (both fields, but separately)
* I cannot submit without fixing problems
* I can see a helper icon (?) or tooltip next to the password field that explains the exact requirements (minimum 10 characters, recommended 12 characters, at least 2 character classes, maximum 50 characters)
    * "I am a human with a password."  is a good password
    * "Let me be your poster boy." is a good password
    * "123456789012"  is a bad password (too generic)
    * "121212121212"  is a bad password (too generic)
    * "1234-7617-1918-2026"  is a good password
    * "MhimcaIlivm." is a good password
* I must solve a local image captcha on registration to prove I am human
* If the captcha image is hard to read, I can click a reload button to get a fresh one without losing my form data

### Registration Existing User

* I am a user that already exists
* When I try to register with an existing email I want to see a message that the email already exists
* I want a link to advance directly to the login flow
* I want access to a "Forgot password?" link directly on the registration page so I can immediately initiate a reset if I've forgotten my old credentials

### Login

* I am an existing user trying to log in
* I want to be able to toggle my password visibility so I can verify I typed it correctly
* When I try to submit an improperly formatted email, I want to see an error before the form submits
* When I enter incorrect credentials, I receive a generic error message so that bad actors cannot use the login form to guess if my account exists
* If I fail to log in consecutively, the server throttles my attempts silently (no visible message explaining the delay — attackers must not learn they are being throttled)

### Password Reset

* I am a user who has forgotten my password
* I can click a "Forgot password?" link on the login page to reach a password reset form
* I enter my email address and receive a confirmation message ("If an account exists, a reset link has been sent") — the wording must be generic to prevent account enumeration
* I receive an email with a time-limited, single-use reset token link
* On the reset page, I enter a new password (subject to the same strength/length requirements as registration) and confirm it
* After successfully resetting, I am redirected to the login page with a success flash message

### Security & Edge Cases

* As a user with disabled JavaScript, a `<noscript>` tag explicitly warns me that the site requires JS to function fully.
* If I bypass the client-side validation, the server still stops me from using a weak password or invalid email format
* When I submit an input with trailing/leading whitespaces (common in copy-paste), they are gracefully trimmed both client-side and server-side to prevent unexpected login failures.
* When I paste excessively long input (e.g. >50 characters) into the password field, the system cleanly rejects it to prevent system strain (SCrypt DoS)
* All form submissions (registration, login, password reset) are protected by CSRF tokens. Requests without a valid token are rejected with HTTP 403.

### Accessibility (a11y)

* As a visually impaired user navigating with a screen reader, I hear real-time updates via `aria-live` regions when my password strength changes (e.g. from "Weak" to "Strong") and when invalid forms are blocked.
* The image Captcha on the registration page includes an accessible bypass or audio/accessible-text alternative to ensure I am not locked out of creating an account.
* Each validation error message is associated with its specific input field via `aria-describedby` and the field is marked with `aria-invalid="true"`, so screen readers announce the error in context.
* When server-side validation fails and the page re-renders, keyboard focus is programmatically moved to the first field containing an error.
* The password strength meter colours meet WCAG AA contrast requirements (minimum 4.5:1 ratio). Strength is communicated via text labels, not colour alone.
* The password visibility toggle, captcha reload button, and all interactive elements are fully operable via keyboard (Tab, Enter, Space).

### Localization

* As an international user (e.g., German, Japanese, or Swedish), I want to see all real-time validation and error messages in my naturally selected language
* If I mistype the captcha, I see a localized error message ("Incorrect captcha, please try again") with a newly generated captcha image, fully localized.

## What Changes

* Add real-time client-side email validation with clear, localised error messages
  (format check, domain-presence check — no ReDoS-vulnerable regex).
* Add a live password-strength meter to the registration form that evaluates passwords
  across multiple dimensions (length, character-class variety) using a non-backtracking,
  O(n) algorithm, and gives the user actionable feedback before they submit.
* Add a password requirements helper icon (?) or tooltip next to the password field, explaining the exact criteria in plain language.
* Add a password visibility toggle allowing the user to view their entered
  password and confirm-password, aiding in accurate input.
* Enforce a meaningful minimum password policy server-side (minimum length 10, recommended length 12, at least two
  distinct character classes) as a hard gate independent of client-side checks. The HTML
  attribute alone is not a safeguard against crafted requests.
* Add a "Confirm password" field to the registration form and validate that both fields
  match client-side before submission is allowed. Also ensure submission is blocked if any
  client-side validation problems are present.
* Replace hardcoded English error strings in `CustomerController` with i18n message
  lookups so localisation is consistent with the rest of the application.
* Apply input length upper bounds server-side (email ≤ 254 chars per RFC 5321,
  password ≤ 50 chars) to prevent application-layer DoS via oversized inputs.
* Support international passwords natively by permitting all Unicode characters (including Japanese Kana/Kanji). Apply `String.prototype.normalize('NFKC')` client-side, and re-apply NFKC normalization server-side BEFORE checking the strict 50-character limit.
* Migrate to natively using SCrypt to completely bypass the 72-byte truncation limit of BCrypt.
* Implement a Server-Rendered Captcha (with zero JS layout shift), HTMX reload button, and a localized error message on captcha failure. The captcha is server-side validated only.
* Implement Progressive Delays (Throttling) on failed login attempts to prevent rapid credential guessing and protect the SCrypt hashing function from DoS. The throttling state must reset to zero upon successful login and absolutely expire after one hour of inactivity. No user-visible message is shown during throttling.
* Implement timing-safe login: when an email is not found in the database, hash a dummy password value before returning the generic error. This ensures the response time is indistinguishable from a real password-mismatch, preventing account enumeration via timing analysis.
* Ensure all POST endpoints (`/register`, `/login`, `/password-reset`) enforce Spring Security CSRF token validation.
* Implement a password reset flow: "Forgot password?" link on login, email-based single-use token with configurable expiry, reset form with the same validation rules, and redirect to login with a success flash message.
* Provide configurable Spring properties for test/dev profiles:
  - `app.throttle.ttl-seconds` (default: 3600 in production, configurable to e.g. 2 seconds in test)
  - `app.captcha.bypass-key` (empty in production; in dev/test, a long secret string that, when submitted as the captcha solution, always passes — enabling automated E2E tests to bypass the captcha without solving it)
* A11y: Use `aria-describedby` to associate error messages with their input fields, set `aria-invalid="true"` on errored fields, programmatically move focus to the first errored field on re-render, ensure WCAG AA contrast on strength meter colours, and ensure all interactive controls are keyboard-operable.

## Security Principles Applied

This change is guided by the following established standards:

| Principle | Source | How it applies here |
|---|---|---|
| Minimum length 10 chars (12 rec.) | NIST SP 800-63B §5.1.1 | Server- and client-side enforcement; replaces the current `minlength="6"` |
| No mandatory complexity rules | NIST SP 800-63B §5.1.1 | Strength meter guides users rather than blocking them with arbitrary rules (e.g. "must contain symbol") |
| Upper length bound on passwords | NIST SP 800-63B §5.1.1 | Max 50 chars enforced server-side to cap SCrypt cost and prevent CPU exhaustion |
| No credential enumeration via registration | OWASP Authentication Cheat Sheet | The "email already in use" error on the registration POST is intentional and acceptable UX in a demo store context (users need to know to log in instead); login errors remain generic |
| Generic login error messages | OWASP Authentication Cheat Sheet | Login POST returns "Invalid email or password" — preserved and i18n-ised |
| Timing-safe authentication | OWASP Authentication Cheat Sheet | Hash a dummy value when the email is unknown so response time does not leak account existence |
| CSRF protection | OWASP Session Management | All state-changing POST endpoints enforce Spring Security CSRF tokens; missing/invalid tokens return HTTP 403 |
| ReDoS-safe validation | OWASP Input Validation | Client-side email and password checks use linear-time character iteration, not backtracking regex |
| Input length validation | OWASP Input Validation | Upper bounds on all string inputs before any logic runs |
| Secure password hashing | OWASP Password Storage | Upgrade hashing algorithm strictly to SCrypt. Implementing Modular Crypt Format (e.g. `{scrypt}` prefixes) allows future algorithm upgrades without requiring immediate DB migrations |
| Permissive Unicode passwords | NIST SP 800-63B §5.1.1 | Permitting all characters, spaces, and emoji; enforcing NFKC normalization to handle full-width/half-width Japanese inputs properly without locking users out |
| Mitigating Authentication DoS | OWASP Authentication | Server-Rendered Captcha (with a11y support) restricts registration to human users. Progressive request throttling nullifies automated credential stuffing on login (expires after 1 hour, silent to user). |
| Secure password reset | OWASP Forgot Password Cheat Sheet | Generic confirmation message prevents enumeration; time-limited, single-use tokens; reset form enforces full password policy |

> **Out of scope for this change**: Session fixation on login (regenerate session ID), IP-based throttle component, account lockout, multi-factor authentication, HTTPS enforcement, Content-Security-Policy headers.
> These belong in separate infrastructure or security hardening changes.

## Capabilities

### New Capabilities

* `registration-validation`: Client-side validation for the account-creation form —
  real-time email format checking, password strength meter with per-criterion feedback,
  and confirm-password match guard. Covers the JS module, Thymeleaf template changes, and
  i18n message keys.
* `password-reset`: End-to-end password reset flow with email-based token delivery,
  reset form validation, and localized messaging.

### Modified Capabilities

* `credit-card-validation`: The credit-card validation JS sets the precedent for how
  client-side validation is structured in this codebase. The new registration validation
  module must follow the same patterns (Allman style, named functions, no backtracking
  regex). No requirement changes — referenced for consistency only, no delta spec needed.

## Impact

* **Templates**: `src/main/resources/templates/customer/register.html` — add confirm-password
  field, wire up new JS module, add strength-meter and validation feedback elements,
  password visibility toggles, captcha with reload button and a11y alternative.
  `src/main/resources/templates/customer/login.html` — add password visibility toggle,
  "Forgot password?" link. New templates for password reset request and reset form.
* **JavaScript**: new file `src/main/resources/static/js/register-validation.js` handling
  both validation rules and visibility toggling.
* **Controller**: `CustomerController` (`/register` POST and `/login` POST) — replace
  hardcoded English error strings with `MessageSource` lookups; add server-side password
  policy enforcement (length and upper bound), input length guards, timing-safe
  comparison, and captcha validation. New `PasswordResetController` for the reset flow.
* **i18n**: `messages.properties` and all locale variants (`de_DE`, `ja_JP`, `sv_SE`) —
  add keys for new client-side validation messages, server-side policy errors,
  captcha failure messages, and password reset messages.
* **Password Scheme Upgrade (Prefixes)** — Migrating `CatalogCustomer` from raw `jbcrypt` to Spring Security's `DelegatingPasswordEncoder`. Hashes will now be prefixed (e.g. `{scrypt}...`). Because the project is in early development, we will intentionally skip historic hash migrations, but this pattern secures crypto-agility for the future.
* **Configuration** — New Spring properties `app.throttle.ttl-seconds` and `app.captcha.bypass-key` with sensible defaults for production and overrides for dev/test profiles.
