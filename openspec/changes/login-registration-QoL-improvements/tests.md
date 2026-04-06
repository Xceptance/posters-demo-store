# Test Cases: Login & Registration QoL Improvements

## Part A: Automated Server-Side Unit Tests

These tests must be executed via JUnit during the CI build process, guaranteeing the underlying Java controllers, services, and formatters enforce strict security rules independently of the frontend.

### A1. Validation & Trimming Logic

* **TC-UNIT-TRIM**: Submitting strings with leading/trailing spaces for email and password are explicitly trimmed before validation occurs.
* **TC-UNIT-LENGTH-BOUNDS**: Submitting an email exceeding 254 chars or a password exceeding 50 chars throws a validation exception *before* reaching the hashing layer.
* **TC-UNIT-NFKC-LIMIT**: Submitting a password that is <50 chars raw but >50 chars *after* NFKC expansion is correctly caught and blocked.
* **TC-UNIT-NFKC-MATCH**: Submitting `Ａ` (full-width) and `A` (half-width) evaluates equivalently during mock credential verification.
* **TC-UNIT-PWD-BOUNDARY-MIN**: A password of exactly 10 characters with 2+ character classes passes validation.
* **TC-UNIT-PWD-BOUNDARY-MAX**: A password of exactly 50 characters (post-NFKC) passes validation.
* **TC-UNIT-PWD-BELOW-MIN**: A password of exactly 9 characters is rejected.
* **TC-UNIT-PWD-ABOVE-MAX**: A password of exactly 51 characters (post-NFKC) is rejected.

### A2. Security & Hardening

* **TC-UNIT-CAPTCHA-VERIFY**: The registration action throws an invalid token error if the user-submitted captcha solution fails to map to the server's actively tracked session captcha map.
* **TC-UNIT-CAPTCHA-BYPASS**: When `app.captcha.bypass-key` is configured (dev/test profile), submitting this key as the captcha solution always passes validation.
* **TC-UNIT-CAPTCHA-BYPASS-OFF**: When `app.captcha.bypass-key` is empty (production profile), submitting any arbitrary string does not bypass the captcha.
* **TC-UNIT-THROTTLE-DELAY**: Hitting the login authentication method with bad credentials increments the throttle counter. At 5 failures, the thread artificially delays. Verify time metrics.
* **TC-UNIT-THROTTLE-RESET**: Resolving a successful login clears the throttle cache for that account instantly.
* **TC-UNIT-THROTTLE-EXPIRY**: With `app.throttle.ttl-seconds` set to 2 seconds in test profile, verify that throttle penalty expires automatically after the configured TTL.
* **TC-UNIT-CRYPTO-AGILITY**: The Spring Security `DelegatingPasswordEncoder` correctly hashes new strings with `{scrypt}`, but successfully reads seeded test entities that use the legacy `{bcrypt}` prefix.
* **TC-UNIT-ACCOUNT-ENUM**: Form submissions for unregistered emails vs wrongly-passworded emails return the exact identical error message string.
* **TC-UNIT-TIMING-SAFE**: Login with a non-existent email takes roughly the same wall-clock time (±20%) as login with a wrong password for an existing email, proving a dummy hash runs when the email is unknown.
* **TC-UNIT-CSRF**: POST to `/register`, `/login`, and `/password-reset` endpoints without a valid CSRF token returns HTTP 403.

### A3. Password Reset

* **TC-UNIT-RESET-TOKEN-GEN**: Requesting a password reset for a valid email creates a time-limited, single-use token in the data store.
* **TC-UNIT-RESET-TOKEN-UNKNOWN-EMAIL**: Requesting a reset for an unknown email does NOT produce an error (returns the same generic success message), preventing enumeration.
* **TC-UNIT-RESET-TOKEN-EXPIRY**: A reset token older than its configured TTL is rejected when the user attempts to use it.
* **TC-UNIT-RESET-TOKEN-SINGLE-USE**: A reset token that has already been used is rejected on second use.
* **TC-UNIT-RESET-PWD-POLICY**: The password reset form enforces the same policy as registration (min 10, max 50, 2+ classes, NFKC normalization).

## Part B: Automated Integration Tests (API Level)

These tests must be executed via `@SpringBootTest` alongside `MockMvc` or `RestAssured` to validate the endpoint behaviors directly without a browser, ensuring the server perfectly handles malicious or headless payloads.

### B1. Edge Cases (Headless Payload Validation)

* **TC-INT-EDGE-PAYLOAD-LIMITS**: Submit an HTTP POST request bypassing frontend limits with a password exceeding 50 characters or email exceeding 254 characters.
   * **Expected**: Request is cleanly rejected early in the controller to prevent CPU exhaustion.
* **TC-INT-EDGE-EMPTY-FIELDS**: Submit an HTTP POST request to `/register` and `/login` missing required fields (e.g., empty string or null email/password).
   * **Expected**: Request is immediately rejected with HTTP 400 Bad Request or standard validation error.
* **TC-INT-EDGE-BAD-FORMAT**: Submit an HTTP POST request with fundamentally invalid formatting (e.g., email lacking `@`, password < 10 characters).
   * **Expected**: Server intercepts and rejects the request using internal format validation rules before hashing or DB lookup occurs.
* **TC-INT-EDGE-CAPTCHA**: Attempt to submit the registration form via POST without a valid captcha solution.
   * **Expected**: Request is immediately rejected with a "Captcha Required" validation error before any SCrypt hashing occurs.
* **TC-INT-EDGE-THROTTLE**: Attempt 5 consecutive failed logins for the same email address in rapid succession via HTTP POST (using test profile with `app.throttle.ttl-seconds=2`).
   * **Expected**: The server responds with progressively longer response times. After the configured TTL elapses, throttle resets.

## Part C: Manual E2E Browser Tests

These tests validate the client-side module, HTML5 attributes, HTMX interactivity, and Accessibility using a real browser. 

> **Important**: During this implementation phase, **these tests will be executed manually**. Once the codebase is converted into a Maven Multi-Module structure (tracked in the backlog), these scenarios will be formalized into the automated browser test suite (using the existing XLT framework).

### C1. Registration Flow

* **TC-E2E-REG-SUCCESS**: Complete a valid registration (valid email, 12-char password with 3 character classes, matching confirm password, correct captcha).
   * **Expected**: Account is created, user is redirected to login page with a success flash message. Auto-login is explicitly prohibited.
* **TC-E2E-REG-EXIST-01**: Submit registration form with an email that already exists.
   * **Expected**: Form submits to the server, but returns an error message stating "Email already exists" with a clickable link to proceed directly to the login page.
* **TC-E2E-REG-EMPTY-01**: Attempt to submit the registration form with one or all fields left blank.
   * **Expected**: Instant inline error indicating that the required fields cannot be empty. Form submission is blocked.
* **TC-E2E-REG-EMAIL-01**: Input an invalid email format (e.g., `user@domain`, `test.com`).
   * **Expected**: Instant inline error indicating "Invalid email format". Form submission is blocked.
* **TC-E2E-REG-PWD-MIN**: Input a password shorter than 10 characters (e.g., 9 characters).
   * **Expected**: Instant inline error indicating "Minimum length is 10 characters".
* **TC-E2E-REG-PWD-MAX**: Input a password longer than 50 characters.
   * **Expected**: Instant inline error indicating length limit. Form submission is blocked.
* **TC-E2E-REG-PWD-MATCH**: Type different values in the "Password" and "Confirm Password" fields.
   * **Expected**: Instant inline error indicating "Passwords do not match".
* **TC-E2E-REG-PWD-TOOLTIP**: Click or hover over the (?) helper icon next to the password field.
   * **Expected**: A tooltip or popover displays the exact requirements: minimum 10 characters, at least 2 character classes, maximum 50 characters.

### C2. Login Flow

* **TC-E2E-LOGIN-SUCCESS**: Submit correct credentials for an existing account.
   * **Expected**: User is authenticated and redirected to the account overview or homepage.
* **TC-E2E-LOGIN-EMPTY**: Attempt to submit the login form with email, password, or both fields left blank.
   * **Expected**: Instant inline error indicating that the fields are required. Form submission is blocked.
* **TC-E2E-LOGIN-EMAIL-FMT**: Input an invalid email format on the login screen.
   * **Expected**: Inline format error. Form submission is blocked.
* **TC-E2E-LOGIN-INVALID-CREDS**: Submit incorrect email or password.
   * **Expected**: Server returns a generic "Invalid email or password" error. (Prevents account enumeration).

### C3. Password Reset Flow

* **TC-E2E-RESET-LINK-LOGIN**: Click the "Forgot password?" link on the login page.
   * **Expected**: User is navigated to the password reset request form.
* **TC-E2E-RESET-LINK-REG**: Click the "Forgot password?" link directly on the registration page (or displayed within the 'Email already exists' warning).
   * **Expected**: User is navigated to the password reset request form.
* **TC-E2E-RESET-REQUEST**: Submit a valid email address on the reset request form.
   * **Expected**: A generic confirmation message is displayed ("If an account exists, a reset link has been sent"). No distinction between existing and non-existing emails.
* **TC-E2E-RESET-FORM**: Follow a valid reset token link and submit a new password meeting all requirements.
   * **Expected**: Password is updated, user is redirected to login with a success flash message.
* **TC-E2E-RESET-EXPIRED**: Follow a reset token link after its expiry.
   * **Expected**: An error message is displayed indicating the link has expired with a link to request a new one.
* **TC-E2E-RESET-VALIDATION**: On the reset form, attempt to submit a password that violates the policy (too short, too long, not enough character classes).
   * **Expected**: Same validation errors as registration apply.

### C4. UI Capabilities & Accessibility

* **TC-E2E-PWD-VIS**: Click the "Show Password" / "Hide Password" toggle on both registration and login forms.
   * **Expected**: Password text becomes visible, then masked once again.
* **TC-E2E-PWD-STRENGTH**: Progressively type a valid password (10+ characters, 2+ character classes).
   * **Expected**: Live password strength meter objectively updates (Weak -> Fair -> Good -> Strong) to guide the user.
* **TC-E2E-PWD-TRIM**: Paste an email or password with leading and trailing whitespaces into the registration form.
   * **Expected**: The whitespaces are automatically and gracefully trimmed client-side prior to submission.
* **TC-E2E-A11Y-METER**: Focus on the password input using a screen reader and type a valid password.
   * **Expected**: The screen reader detects the `aria-live` region and actively announces the strength.
* **TC-E2E-A11Y-CAPTCHA**: Attempt to complete registration using the captcha's accessible bypass (audio/text alternative).
   * **Expected**: The alternative is usable and allows correct form submission without relying on vision.
* **TC-E2E-A11Y-FOCUS**: Submit a registration form with server-side validation errors (e.g., via a stale captcha).
   * **Expected**: After re-render, keyboard focus is programmatically moved to the first field with an error.
* **TC-E2E-A11Y-KEYBOARD**: Using only the keyboard (Tab, Enter, Space), operate the password visibility toggle, captcha reload button, and form submission.
   * **Expected**: All interactive elements are reachable and operable without a mouse.
* **TC-E2E-NO-JS**: Disable JavaScript and load the registration page.
   * **Expected**: A `<noscript>` static banner explicitly warns the user that JS is required to fully use the site.

### C5. Captcha

* **TC-E2E-CAPTCHA-RELOAD**: Click the captcha reload button on the registration form.
   * **Expected**: A new captcha image renders via HTMX swap without losing any form data already entered.
* **TC-E2E-CAPTCHA-FAIL**: Submit the registration form with an incorrect captcha solution.
   * **Expected**: A localized error message ("Incorrect captcha, please try again") is displayed, and a fresh captcha image is rendered. The rest of the form data is preserved.

### C6. Localization & Unicode

* **TC-E2E-I18N-LANGS**: Switch browser locale between German (`de_DE`), Japanese (`ja_JP`), and Swedish (`sv_SE`) and trigger validation errors on registration, login, and password reset.
   * **Expected**: Validation warnings, server errors, captcha failure messages, and password reset messages display correctly in the selected language without falling back to raw message keys.
* **TC-E2E-UNI-PERMIT**: Submit a registration with a password containing Japanese Hiragana and Kanji (e.g. `パスワードテスト`).
   * **Expected**: Form is submitted successfully and account is created.
