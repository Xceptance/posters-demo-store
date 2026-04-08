# Registration Accessibility and UI

This test verifies the functionality and compliance of the accessibility (WCAG) features baked into the registration form, particularly verifying screen-reader attributes on the dynamic password visibility toggles.

## Metadata

- **Test ID:** TC_ACC_003
- **Version:** 1.0
- **Software Version:** >= 1.0.0
- **Domains:** Account
- **Priority:** 🟡 Medium
- **Status:** 📝 Draft
- **Execution Type:** Manual
- **Suite:** 🧠 Sanity, 🔄 Regression, ♿ Accessibility
- **Requirements:**
  - [REQ-ID or link to backlog item]
- **Tags:** `account`, `registration`, `accessibility`, `ui`
- **Author:** Antigravity (AI) (2026-04-08)
- **Reviewers:**
  - [Human]

## Comments

> [!TIP]
> You will need to inspect the DOM (Developer Tools -> Elements) during this test to verify the `aria-label` changing inside the `<button>` element.

## Preconditions

- The Posters Demo Store is running.
- The Registration page `/{locale}/register` is open.

## Test Data

- **Input String:** `secret-vision`

## Execution Targets

**Target Locales:**
- [x] EN-US

**Target Viewports:**
- [x] Desktop (1920x1080)
- [x] Mobile (375x812)

## Steps

### 1. Enter Password Data
- **Action:** Type the **Input String** into the Password field.
- **Verify:** The content is visually masked with dots/asterisks by default (`type="password"`).

### 2. Inspect Hidden State Attributes
- **Action:** Inspect the `btn-toggle-password` HTML element next to the input.
- **Verify:** The `<button>` should currently possess the attribute `aria-label="Show password"`.
- **Verify:** The inner icon should have the class `bi-eye`.

### 3. Trigger Visibility Toggle
- **Action:** Click the "Show Password" eye icon on the UI.
- **Verify:** The content of the password field visually unmasks (displays as `secret-vision` because `type="text"`).

### 4. Inspect Unmasked State Attributes
- **Action:** Re-inspect the `btn-toggle-password` element in the DOM.
- **Verify:** The `<button>` attribute must have instantly switched to `aria-label="Hide password"`.
- **Verify:** The inner icon class has swapped to `bi-eye-slash`.

### 5. Revert State
- **Action:** Click the toggle icon again.
- **Verify:** The input returns to `#2` standard behavior (masked, `aria-label="Show password"`, `bi-eye`).

---

## Pass/Fail Criteria

- **Pass:** The `<input>` type toggles completely synchronized alongside the `aria-label` and visual Bootstrap Icons, ensuring a screen-reader correctly detects the explicit state of the button.
- **Fail:** The attributes fail to swap dynamically or the javascript throws an error halting the visual transformation.

---

## Postconditions

- The visual state returns to hidden `type="password"`.

---

## Related Cases

- [TC_ACC_001: Valid Account Registration](./TC_ACC_001_Registration_Happy_Path.md)
