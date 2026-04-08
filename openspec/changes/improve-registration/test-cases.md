# Registration Flow Test Cases

This document defines explicit test cases derived from our user stories. These steps are designed for manual QA verification and can be directly translated into end-to-end browser tests using tools like XLT, Playwright, or Cypress.

## 1. Validation & UI Feedback

### TC-1.1: Instant Inline Format Validation (US-01)
**Preconditions:** None
1. Navigate to the Registration page.
2. Start typing an invalid email (e.g., `test@`).
3. Pause or click outside the input field (triggering `blur` or `keyup delay`).
**Expected:** An inline error message (e.g., "Please enter a valid email format") appears immediately below the email field without a full page reload. The screen reader `aria-live` region announces the error.

### TC-1.2: Password Visibility Toggle (US-03)
**Preconditions:** None
1. Navigate to the Registration page.
2. Type `SecretPassword123!` into the password field.
**Expected:** The input characters are masked (`type="password"`).
3. Click the "eye-icon" toggle on the password field.
**Expected:** `SecretPassword123!` is revealed as plain text (`type="text"`). The eye-icon visual state updates, and the `aria-pressed` attribute is set to `true`.
4. Click the toggle again.
**Expected:** The characters are masked again (`type="password"`).

## 2. Magic Link Verification Flow

### TC-2.1: Successful Registration and Activation (US-05)
**Preconditions:** Access to a valid test email inbox.
1. Submit the registration form with a valid, unregistered email and strong password.
**Expected:** System redirects to a "Check your email" success screen. The account is stored in a `PENDING` state.
2. Retrieve the magic link from the test email inbox.
3. Click the magic link.
**Expected:** The user is logged in automatically and redirected to the home/dashboard page. The account state officially becomes `ACTIVE`.

### TC-2.2: Premature Login Attempt
**Preconditions:** Account is registered but in `PENDING` state (activation link ignored).
1. Navigate to the Login screen.
2. Enter the email and password for the pending account.
3. Click "Login".
**Expected:** Login is rejected. A prominent error message prompts the user to check their email for their activation link.

### TC-2.3: Expired or Invalid Magic Link
**Preconditions:** Account is registered but in `PENDING` state.
1. Navigate to a broken or tampered magic link (e.g., `.../activate?token=INVALID999`).
**Expected:** An error page is displayed stating the link is invalid/expired.
2. Verify that there is a "Resend Verification Email" button on the screen.

## 3. Account Existence & Friction Reduction

### TC-3.1: Soft Redirect on Existing Email (US-06)
**Preconditions:** An account with `existing@example.com` already exists.
1. Navigate to the Registration page.
2. Submit the form using `existing@example.com`.
**Expected:** The system does NOT show an inline error. The user is redirected to the Login page. 
**Expected:** A friendly toast notification appears ("Looks like you already have an account!").
**Expected:** The Email input field on the login form is pre-filled with `existing@example.com`.

### TC-3.2: Alternative Navigation Links (US-07, US-08)
**Preconditions:** None
1. Navigate to the Registration page.
2. Click the "Forgot Password?" link below the form.
**Expected:** User is safely navigated to the password reset flow.
3. Navigate back to the Registration page.
4. Click the "Already a member? Log in" link.
**Expected:** User is safely navigated to the standard login screen.

## 4. Accessibility & Mobile

### TC-4.1: Mobile Keyboard Optimization (US-02)
**Preconditions:** Use a mobile device touch-emulator (Chrome DevTools) or physical phone.
1. Navigate to the Registration page.
2. Focus the "Email address" input field.
**Expected:** The mobile keyboard displayed must be the email-specific variant (displaying `@` and `.` keys prominently, typically achieved via `type="email"`).
