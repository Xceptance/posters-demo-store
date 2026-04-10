# Registration Form Accessibility and Keyboard Navigation

Verifies that the registration form is fully interactive using only the keyboard, ensuring accessibility and rapid entry for power users.

## Metadata

- **Test ID:** TC_ACC_010
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🟡 Medium
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** ♿ Accessibility
- **Requirements:**
  - WCAG Compliance
- **Tags:** `registration`, `accessibility`, `keyboard`
- **Author:** Antigravity (AI) (2026-04-09)
- **Reviewers:**
  - 

## Comments

> [!CAUTION]
> The "Password Visibility" toggle (eye icon) must be reachable via the `Tab` key and triggerable via `Space` or `Enter`. If it was implemented as an untabbable raw `<i>` tag without a button, this test will fail.

## Preconditions

- The Posters Demo Store is running.
- The user is on the Account Creation page.
- Focus is set to the browser address bar.

## Test Data

- (Standard valid registration data to type)

## Execution Targets

**Target Locales:**
- [x] EN-US
- [x] EN-GB
- [x] DE-DE
- [x] SV-SE

**Target Viewports:**
- [x] Desktop (Large)

---

## Steps

### 1. Tab into Form
- **Action:** Continually press the `Tab` key.
- **Verify:** Focus cycles into the form, landing first on "First Name", then "Last Name", then "Email", then "Password" in logical DOM order. Highlight rings are visibly indicating current focus.

### 2. Enter Data via Keyboard
- **Action:** Type values into the fields, using `Tab` to jump to the next input. 
- **Verify:** Inputs capture keystrokes perfectly.

### 3. Toggle Password Visibility via Keyboard
- **Action:** While focused on the Password field, hit `Tab`. 
- **Verify:** Focus lands on the "Password visibility toggle" (eye icon).
- **Action:** Press `Space` or `Enter` on the icon.
- **Verify:** The password input correctly toggles from masked to visible.

### 4. Submit via Enter Key
- **Action:** Hit `Tab` until focus is on the "Create Account" button (or while focus is inside any input field). Press `Enter`.
- **Verify:** Form submission is triggered successfully.

---

## Pass/Fail Criteria

- **Pass:** The entire registration workflow can be completed successfully without using a mouse.
- **Fail:** Elements (like the show password icon or submit button) are skipped by the tab index or cannot be engaged via keyboard. Focus visibility relies on browser defaults that outline the element clearly.

---

## Postconditions

- User account created successfully.

---

## Related Cases

- [TC_ACC_005: Password Visibility Toggle](./TC_ACC_005.md)

---

## Change History

| Date | Version | Author | Description |
| :--- | :--- | :--- | :--- |
| 2026-04-09 | 1.0 | Antigravity (AI) | Initial creation |
